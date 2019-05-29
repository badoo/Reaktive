import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType

@Suppress("UnstableApiUsage")
abstract class IosPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureIosCompilation(target)
    }

    private fun configureIosCompilation(target: Project) {
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            iosArm32(name = "ios32") {
                binaries {
                    framework()
                }
            }
            iosArm32(name = "ios64") {
                binaries {
                    framework()
                }
            }
            iosX64(name = "iosSim") {
                binaries {
                    framework()
                    executable(buildTypes = setOf(NativeBuildType.DEBUG))

                    val testBinariesTaskName = compilations.getByName("test").binariesTaskName
                    target.tasks.register("iosTest", RunIosTestTask::class.java) {
                        group = "verification"
                        dependsOn(target.tasks.named(testBinariesTaskName))
                        testExecutables.from(getExecutable("test", NativeBuildType.DEBUG).outputFile)
                    }
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
                project.exec {
                    commandLine("xcrun", "simctl", "spawn", "iPhone X", *files)
                }
            } else {
                logger.error("No test executable for iOS")
            }
        }
    }
}