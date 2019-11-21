package tasks

import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

abstract class BuildIosSampleTask : AbstractTask() {

    init {
        group = LifecycleBasePlugin.BUILD_GROUP
    }

    @TaskAction
    open fun buildSample() {
        val dir = project.file("sample-ios-app")
        project.exec {
            workingDir = dir
            commandLine(
                "pod",
                "deintegrate"
            )
        }
        project.exec {
            workingDir = dir
            commandLine(
                "pod",
                "install",
                "--repo-update"
            )
        }
        project.exec {
            workingDir = dir
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
