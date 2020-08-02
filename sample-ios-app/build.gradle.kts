import com.badoo.reaktive.configuration.Target
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask

open class BuildIosSampleTask : DefaultTask() {

    @InputDirectory
    val sources: File = project.file("sample-ios-app")

    @InputDirectory
    val releaseFramework: Property<File> = project.objects.property()

    @InputDirectory
    val debugFramework: Property<File> = project.objects.property()

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

if (Target.shouldDefineTarget(project, Target.IOS)) {
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
}
