import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File
import javax.inject.Inject

@Suppress("UnstableApiUsage")
abstract class DarwinPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureDarwinCompilation(target)
    }

    private fun configureDarwinCompilation(target: Project) {
        val buildBinariesTasks = mutableListOf<String>()
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            if (Target.shouldDefineTarget(target, Target.IOS)) {
                iosArm32(TARGET_NAME_IOS_ARM32).configureFramework(buildBinariesTasks)
                iosArm64(TARGET_NAME_IOS_ARM64).configureFramework(buildBinariesTasks)
                iosX64(TARGET_NAME_IOS_X64).configureFramework(buildBinariesTasks, withTest = true)
            }
            if (Target.shouldDefineTarget(target, Target.WATCHOS)) {
                watchosArm32(TARGET_NAME_WATCHOS_ARM32).configureFramework(buildBinariesTasks)
                watchosArm64(TARGET_NAME_WATCHOS_ARM64).configureFramework(buildBinariesTasks)
                watchosX86(TARGET_NAME_WATCHOS_SIM).configureFramework(buildBinariesTasks, withTest = true)
            }
            if (Target.shouldDefineTarget(target, Target.TVOS)) {
                tvosArm64(TARGET_NAME_TVOS_ARM64).configureFramework(buildBinariesTasks)
                tvosX64(TARGET_NAME_TVOS_X64).configureFramework(buildBinariesTasks, withTest = true)
            }
            if (Target.shouldDefineTarget(target, Target.MACOS)) {
                macosX64(TARGET_NAME_MACOS_X64).configureFramework(buildBinariesTasks)
            }
        }
        setupBuildAll(target, buildBinariesTasks)
        setupBuildFat(target)
    }

    private fun KotlinNativeTarget.configureFramework(buildTasks: MutableCollection<String>, withTest: Boolean = false) {
        binaries {
            framework()
            if (withTest) {
                setupTest(this@configureFramework, this, project)
            }
        }
        buildTasks += compilations.getByName(SourceSet.MAIN_SOURCE_SET_NAME).binariesTaskName
    }

    private fun setupTest(
        kotlinNativeTarget: KotlinNativeTarget,
        kotlinNativeBinaryContainer: KotlinNativeBinaryContainer,
        target: Project
    ) {
        if (kotlinNativeTarget.publishable) {
            val testBinariesTaskName = kotlinNativeTarget.compilations.getByName(SourceSet.TEST_SOURCE_SET_NAME).binariesTaskName
            val testBinariesFile = kotlinNativeBinaryContainer.getTest(NativeBuildType.DEBUG).outputFile
            val konanTarget = kotlinNativeTarget.konanTarget
            val appleTestProvider = target
                .tasks
                .register(
                    DarwinTestTask.createNameFromTarget(konanTarget),
                    DarwinTestTask::class.java,
                    testBinariesFile,
                    konanTarget
                )
                .apply { configure { dependsOn(target.tasks.named(testBinariesTaskName)) } }
            target.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
                dependsOn(appleTestProvider)
            }
        }
    }

    private fun setupBuildAll(
        target: Project,
        buildTasks: Collection<String>
    ) {
        val allBinariesTaskProvider = target.tasks.register(TASK_NAME_IOS_BINARIES) {
            dependsOn(buildTasks.map { target.tasks.named(it) })
        }
        target.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
            dependsOn(allBinariesTaskProvider)
        }
    }

    private fun setupBuildFat(target: Project) {
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            targets.withType(KotlinNativeTarget::class.java).configureEach {
                if (konanTarget == KonanTarget.MACOS_X64) {
                    // macOS does not support fat framework
                    return@configureEach
                }
                binaries.withType(Framework::class.java).configureEach {
                    val framework = this
                    val binaryBaseName = baseName.takeIf { it != target.name } ?: ""
                    val buildType = buildType.name.toLowerCase()
                    val family = konanTarget.family.toString().toLowerCase()
                    val taskName = "$TASK_NAME_FAT${binaryBaseName.capitalize()}${family.capitalize()}${buildType.capitalize()}"
                    // get or create fatBaseNameFamilyBuildType task
                    val provider =
                        try {
                            target.tasks.named(taskName, FatFrameworkTask::class.java)
                        } catch (unknownException: UnknownDomainObjectException) {
                            target.tasks.register(taskName, FatFrameworkTask::class.java) {
                                baseName = framework.baseName
                                // Skip base name, if it was not changed
                                // e.g. fatDebug, fatCustomBaseNameDebug
                                val binaryNameFolder = if (binaryBaseName.isNotEmpty()) "$binaryBaseName/" else ""
                                destinationDir = project.file(
                                    "${project.buildDir}/fat-framework/$binaryNameFolder$family/$buildType"
                                )
                            }
                        }
                    // Add current framework to fat output
                    provider.configure { from(framework) }
                }
            }
        }
    }

    open class DarwinTestTask @Inject constructor(
        private val testExecutable: File,
        private val target: KonanTarget
    ) : AbstractTask() {

        // KonanTarget is not serializable property, so we need to provide string representation for caching
        @get:[Input]
        val targetSerialized: String
            get() = target.name

        // There is no SkipWhenEmpty equivalent for single file, use workaround
        @get:[SkipWhenEmpty InputFiles]
        val testExecutables: Collection<File>?
            get() = listOf(testExecutable).takeIf { testExecutable.exists() }

        // We can't run multiple DarwinTestTask of same target in parallel due emulator restriction
        // Synchronize with OutputDirectory workaround until SharedResource is released
        // https://discuss.gradle.org/t/disabling-parallel-for-certain-tasks-only/10151/7
        @get:OutputDirectory
        val synchronizationDirectory: File by lazy {
            val parentFolder = File(project.rootProject.buildDir, "darwin-emulator-sync")
            val targetFolder = File(parentFolder, target.name)
            targetFolder
        }

        init {
            group = LifecycleBasePlugin.VERIFICATION_GROUP
        }

        @TaskAction
        fun runTest() {
            val device = getEmulatorNameForTarget(target)
            val bootResult = project.exec { commandLine("xcrun", "simctl", "boot", device) }
            try {
                val spawnResult = project.exec { commandLine("xcrun", "simctl", "spawn", device, testExecutable) }
                spawnResult.assertNormalExitValue()
            } finally {
                if (bootResult.exitValue == 0) {
                    project.exec { commandLine("xcrun", "simctl", "shutdown", device) }
                }
            }
        }

        companion object {
            fun createNameFromTarget(target: KonanTarget) =
                target
                    .name
                    .split('_')
                    .mapIndexed { index, text -> if (index > 0) text.capitalize() else text }
                    .reduce { acc, text -> "$acc$text" } + "Test"

            fun getEmulatorNameForTarget(target: KonanTarget) =
                when (target) {
                    is KonanTarget.IOS_X64 -> "iPhone 8"
                    is KonanTarget.WATCHOS_X86 -> "Apple Watch Series 5 - 44mm"
                    is KonanTarget.TVOS_X64 -> "Apple TV"
                    else -> throw IllegalArgumentException("$target is not testable by xcrun")
                }
        }
    }

    companion object {
        const val TARGET_NAME_IOS_ARM32 = "ios32"
        const val TARGET_NAME_IOS_ARM64 = "ios64"
        const val TARGET_NAME_IOS_X64 = "iosSim"
        const val TARGET_NAME_WATCHOS_ARM32 = "watchosArm32"
        const val TARGET_NAME_WATCHOS_ARM64 = "watchosArm64"
        const val TARGET_NAME_WATCHOS_SIM = "watchosSim"
        const val TARGET_NAME_TVOS_ARM64 = "tvosArm64"
        const val TARGET_NAME_TVOS_X64 = "tvosSim"
        const val TARGET_NAME_MACOS_X64 = "macosX64"

        const val TASK_NAME_IOS_BINARIES = "iosBinaries"
        const val TASK_NAME_FAT = "fat"
    }
}
