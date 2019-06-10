import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinNativeBinaryContainer
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

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
            testExecutables.from(
                kotlinNativeBinaryContainer.getExecutable(
                    SourceSet.TEST_SOURCE_SET_NAME,
                    NativeBuildType.DEBUG
                ).outputFile
            )
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

    abstract class RunIosTestTask : AbstractTask() {

        @InputFiles
        open var testExecutables: ConfigurableFileCollection = project.objects.fileCollection()

        @TaskAction
        open fun runTest() {
            val files = testExecutables.filter { it.exists() }.files.toTypedArray()
            if (files.isNotEmpty()) {
                project.exec {
                    commandLine("xcrun", "simctl", "spawn", "iPhone X", *files)
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
    }
}