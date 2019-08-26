import com.android.utils.appendCapitalized
import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.SourceSet
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinJsOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

@Suppress("UnstableApiUsage")
abstract class JsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureJsCompilation(target)
        configureJsTest(target)
    }

    private fun configureJsCompilation(target: Project) {
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(TARGET_NAME_JS) {
                browser()
            }
            sourceSets.getByName(TARGET_NAME_JS.appendCapitalized(SourceSet.MAIN_SOURCE_SET_NAME)) {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                }
            }
            sourceSets.getByName(TARGET_NAME_JS.appendCapitalized(SourceSet.TEST_SOURCE_SET_NAME)) {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
        target.tasks.named(TASK_COMPILE_KOTLIN_JS, Kotlin2JsCompile::class.java) {
            kotlinOptions.configure()
        }
        target.tasks.named(TASK_COMPILE_KOTLIN_JS_TEST, Kotlin2JsCompile::class.java) {
            kotlinOptions.configure()
        }
    }

    private fun configureJsTest(target: Project) {
        // workaround for https://github.com/srs/gradle-node-plugin/issues/301
        target.repositories.whenObjectAdded {
            if (this is IvyArtifactRepository) {
                metadataSources {
                    artifact()
                }
            }
        }

        target.pluginManager.apply(NodePlugin::class.java)
        target.extensions.configure(NodeExtension::class.java) {
            download = true
            version = "12.2.0"
        }

        val dependenciesTask = target.tasks.register("installJsTestDependencies", NpmTask::class.java) {
            setArgs(
                listOf(
                    "install",
                    "kotlin@${project.property("kotlin_version")}",
                    "kotlin-test@${project.property("kotlin_version")}",
                    "mocha@6.1.4"
                )
            )
        }

        val nodeModulesTask = target.tasks.register("populateNodeModules", CopyLocalNodeModulesTask::class.java) {
            // dependenciesTask remove all files from node_modules that are not defined in package.json
            // that is why dependenciesTask should be executed before this task
            dependsOn(dependenciesTask)
        }

        val compileTestJsTask = target.tasks.named(TASK_COMPILE_KOTLIN_JS_TEST, Kotlin2JsCompile::class.java)

        val runMochaTask = target.tasks.register("runMocha", RunMochaTask::class.java) {
            dependsOn(dependenciesTask, compileTestJsTask, nodeModulesTask)
            group = LifecycleBasePlugin.VERIFICATION_GROUP
            testJsFiles.from(compileTestJsTask.get().outputFile)
        }

        target.tasks.named(TASK_NAME_JS_TEST) {
            // We don't support new JS test runners because of module naming issues
            // NodeJsRootExtension creates reaktive-test folder as output for test sources of reaktive
            // and reaktive-test folder as output for main sources of reaktive-test
            onlyIf { false }
        }

        target.tasks.named(LifecycleBasePlugin.CHECK_TASK_NAME) {
            dependsOn(runMochaTask)
        }
    }

    private fun KotlinJsOptions.configure() {
        metaInfo = true
        sourceMap = true
        sourceMapEmbedSources = "always"
        moduleKind = "umd"
        main = "call"
    }

    /**
     * Copy all js files from current project and project dependencies
     * into [CopyLocalNodeModulesTask.FOLDER_NODE_MODULES]
     * to make them discoverable by NodeJS.
     */
    @Suppress("LeakingThis")
    abstract class CopyLocalNodeModulesTask : Copy() {

        init {
            val compileTasks = project
                .configurations
                .asSequence()
                .filter {
                    // quite fragile check, but I don't know better way to filter out only JS configurations
                    it.name.contains("js", ignoreCase = true)
                }
                .flatMap { it.allDependencies.asSequence() }
                .filterIsInstance<ProjectDependency>()
                .distinctBy { it.name }
                .map { it.dependencyProject.getCompileJsTask() }
                .toMutableList()
                .plus(project.getCompileJsTask())

            dependsOn(compileTasks)
            from(compileTasks.map { it.outputFile })
            into(FOLDER_NODE_MODULES)
        }

        private fun Project.getCompileJsTask(): Kotlin2JsCompile =
            tasks.named(TASK_COMPILE_KOTLIN_JS, Kotlin2JsCompile::class.java).get()

        private companion object {
            private const val FOLDER_NODE_MODULES = "node_modules"
        }
    }

    /**
     * Execute provided test files with mocha test framework.
     */
    abstract class RunMochaTask : NodeTask() {

        @InputFiles
        open var testJsFiles: ConfigurableFileCollection = project.objects.fileCollection()

        override fun exec() {
            val files = testJsFiles.mapNotNull {
                if (it.exists()) {
                    it.relativeTo(project.projectDir)
                } else {
                    logger.error("Test file ($it) was not found!")
                    null
                }
            }
            if (files.isNotEmpty()) {
                // these configuration parameters are not tasks input
                setScript(project.file(PATH_MOCHA))
                setArgs(files)
                super.exec()
            }
        }
    }

    companion object {
        private const val TASK_COMPILE_KOTLIN_JS = "compileKotlinJs"
        private const val TASK_COMPILE_KOTLIN_JS_TEST = "compileTestKotlinJs"
        private const val PATH_MOCHA = "node_modules/mocha/bin/mocha"

        const val TARGET_NAME_JS = "js"

        const val TASK_NAME_JS_TEST = "jsTest"
    }

}
