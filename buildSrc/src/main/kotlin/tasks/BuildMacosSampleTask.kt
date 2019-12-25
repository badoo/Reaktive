package tasks

import org.gradle.api.internal.AbstractTask
import org.gradle.api.tasks.TaskAction
import org.gradle.language.base.plugins.LifecycleBasePlugin

abstract class BuildMacosSampleTask : AbstractTask() {

    init {
        group = LifecycleBasePlugin.BUILD_GROUP
    }

    @TaskAction
    open fun buildSample() {
        val dir = project.file("sample-macos-app")
        project.exec {
            workingDir = dir
            commandLine(
                "xcodebuild",
                "-project",
                "sample-macos-app.xcodeproj",
                "-scheme",
                "sample-macos-app"
            )
        }
    }
}
