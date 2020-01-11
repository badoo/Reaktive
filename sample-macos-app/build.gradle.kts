import org.gradle.api.internal.AbstractTask

open class BuildMacosSampleTask : AbstractTask() {

    @InputDirectory
    val sources: File = project.file("sample-macos-app")

    @InputDirectory
    lateinit var releaseFramework: Provider<File>

    @InputDirectory
    lateinit var debugFramework: Provider<File>

    init {
        group = LifecycleBasePlugin.BUILD_GROUP
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
    releaseFramework = binariesTasks.map { it.project.file("build/bin/macosX64/releaseFramework") }
    debugFramework = binariesTasks.map { it.project.file("build/bin/macosX64/debugFramework") }
}
