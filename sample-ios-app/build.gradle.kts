import org.gradle.api.internal.AbstractTask
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask

open class BuildIosSampleTask : AbstractTask() {

    @InputDirectory
    val sources: File = project.file("sample-ios-app")

    @InputDirectory
    lateinit var fatDebug: Provider<File>

    @InputDirectory
    lateinit var fatRelease: Provider<File>

    init {
        group = LifecycleBasePlugin.BUILD_GROUP
    }

    @TaskAction
    open fun buildSample() {
        project.exec {
            commandLine(
                "pod",
                "deintegrate"
            )
        }
        project.exec {
            commandLine(
                "pod",
                "install",
                "--repo-update"
            )
        }
        project.exec {
            commandLine(
                "xcodebuild",
                "-workspace",
                "sample-ios-app.xcworkspace",
                "-scheme",
                "sample-ios-app",
                "-sdk",
                "iphonesimulator"
            )
        }
    }
}

tasks.register<BuildIosSampleTask>("build") {
    val sampleMppModuleTasks = project(":sample-mpp-module").tasks
    fatDebug =
        sampleMppModuleTasks
            .named<FatFrameworkTask>("fatIosDebug")
            .map { it.fatFrameworkDir }
    fatRelease =
        sampleMppModuleTasks
            .named<FatFrameworkTask>("fatIosRelease")
            .map { it.fatFrameworkDir }
}
