import com.badoo.reaktive.configuration.Target
import org.gradle.internal.os.OperatingSystem

open class BuildIosSampleTask : DefaultTask() {

    @InputDirectory
    val sources: File = project.file("sample-ios-app")

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
                "iphonesimulator",
                "-arch",
                "x86_64"
            )
        }
    }
}

if (OperatingSystem.current().isMacOsX && Target.shouldDefineTarget(project, Target.IOS)) {
    tasks.register<BuildIosSampleTask>("build")
}
