import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.internal.AbstractTask

open class BuildMacosSampleTask : AbstractTask() {

    @InputDirectory
    val sources: File = project.file("sample-macos-app")

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
                "xcodebuild",
                "-project",
                "sample-macos-app.xcodeproj",
                "-scheme",
                "sample-macos-app"
            )
        }
    }
}

tasks.register<BuildMacosSampleTask>("build") {
    val binariesTasks = project(":sample-mpp-module").tasks.named("macosX64MainBinaries")
    // macosX64MainBinaries does not define any outputs, hardcode them
    releaseFramework.set(binariesTasks.map { it.project.file("build/bin/macosX64/releaseFramework") })
    debugFramework.set(binariesTasks.map { it.project.file("build/bin/macosX64/debugFramework") })
}
