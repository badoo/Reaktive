import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.internal.AbstractTask
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask

open class BuildIosSampleTask : AbstractTask() {

    @InputDirectory
    val sources: File = project.file("sample-ios-app")

    @InputDirectory
    val releaseFramework: Property<File> = project.objects.property()

    @InputDirectory
    val debugFramework: Property<File> = project.objects.property()

    init {
        group = LifecycleBasePlugin.BUILD_GROUP
        onlyIf { Os.isFamily(Os.FAMILY_MAC) }
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
    releaseFramework.set(
        sampleMppModuleTasks
            .named<FatFrameworkTask>("fatIosRelease")
            .map { it.fatFrameworkDir }
    )
    debugFramework.set(
        sampleMppModuleTasks
            .named<FatFrameworkTask>("fatIosDebug")
            .map { it.fatFrameworkDir }
    )
}
