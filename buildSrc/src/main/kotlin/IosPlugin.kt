import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask

@Suppress("UnstableApiUsage")
abstract class IosPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureIosCompilation(target)
    }

    private fun configureIosCompilation(target: Project) {
        val buildBinariesTasks = mutableListOf<String>()
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            iosArm32(TARGET_NAME_X32) {
                binaries {
                    framework()
                }
                buildBinariesTasks += compilations.getByName(SourceSet.MAIN_SOURCE_SET_NAME).binariesTaskName
            }
            iosArm64(TARGET_NAME_X64) {
                binaries {
                    framework()
                }
                buildBinariesTasks += compilations.getByName(SourceSet.MAIN_SOURCE_SET_NAME).binariesTaskName
            }
            iosX64(TARGET_NAME_SIM) {
                binaries {
                    framework()
                    setupTest(this@iosX64, this, target)
                }
                buildBinariesTasks += compilations.getByName(SourceSet.MAIN_SOURCE_SET_NAME).binariesTaskName
            }
        }
        setupBuildAll(target, buildBinariesTasks)
        setupBuildFat(target)
    }

    private fun setupTest(
        kotlinNativeTarget: KotlinNativeTarget,
        kotlinNativeBinaryContainer: KotlinNativeBinaryContainer,
        target: Project
    ) {
        val testBinariesTaskName =
            kotlinNativeTarget.compilations.getByName(SourceSet.TEST_SOURCE_SET_NAME).binariesTaskName
        val iosTestProvider = target.tasks.register(TASK_NAME_IOS_TEST, RunIosTestTask::class.java) {
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            dependsOn(target.tasks.named(testBinariesTaskName))
            testExecutables.from(kotlinNativeBinaryContainer.getTest(NativeBuildType.DEBUG).outputFile)
        }
        if (kotlinNativeTarget.publishable) {
            target.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
                dependsOn(iosTestProvider)
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
                binaries.withType(Framework::class.java).configureEach {
                    val framework = this
                    val binaryBaseName = baseName.takeIf { it != target.name } ?: ""
                    val buildType = buildType.name.toLowerCase()
                    val taskName = "$TASK_NAME_FAT${binaryBaseName.capitalize()}${buildType.capitalize()}"
                    // get or create fat-X-Y task
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
                                    "${project.buildDir}/fat-framework/$binaryNameFolder$buildType"
                                )
                            }
                        }
                    // Add current framework to fat output
                    provider.configure { from(framework) }
                }
            }
        }
    }

    abstract class RunIosTestTask : AbstractTask() {

        @InputFiles
        open var testExecutables: ConfigurableFileCollection = project.objects.fileCollection()

        @TaskAction
        open fun runTest() {
            val files = testExecutables.filter { it.exists() }.files.toTypedArray()
            if (files.isNotEmpty()) {
                val bootResult = project.exec { commandLine("xcrun", "simctl", "boot", "iPhone 8") }
                try {
                    val spawnResult = project.exec { commandLine("xcrun", "simctl", "spawn", "iPhone 8", *files) }
                    spawnResult.assertNormalExitValue()
                } finally {
                    if (bootResult.exitValue == 0) {
                        project.exec { commandLine("xcrun", "simctl", "shutdown", "iPhone 8") }
                    }
                }
            } else {
                logger.error("No test executable for iOS")
            }
        }
    }

    companion object {
        const val TARGET_NAME_X32 = "ios32"
        const val TARGET_NAME_X64 = "ios64"
        const val TARGET_NAME_SIM = "iosSim"

        const val TASK_NAME_IOS_BINARIES = "iosBinaries"
        const val TASK_NAME_IOS_TEST = "iosTest"
        const val TASK_NAME_FAT = "fat"
    }
}
