import com.moowork.gradle.node.NodeExtension
import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.npm.NpmTask
import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.IvyArtifactRepository
import org.gradle.api.tasks.Copy
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
            targetFromPreset(presets.getByName("js"), "js")
            sourceSets.getByName("jsMain").dependencies {
                implementation(kotlin("stdlib-js"))
            }
            sourceSets.getByName("jsTest").dependencies {
                implementation(kotlin("test-js"))
            }
        }
        target.tasks.named("compileKotlinJs", Kotlin2JsCompile::class.java) {
            kotlinOptions.configure()
        }
        target.tasks.named("compileTestKotlinJs", Kotlin2JsCompile::class.java) {
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
            version = Versions.node
        }

        val dependenciesTask = target.tasks.register("installJsTestDependencies", NpmTask::class.java) {
            setArgs(
                listOf(
                    "install",
                    "kotlin@${Versions.kotlin}",
                    "kotlin-test@${Versions.kotlin}",
                    "mocha@${Versions.mocha}"
                )
            )
        }

        val nodeModuleTask = target.tasks.register("populateNodeModule", Copy::class.java) {
            val compileTask = target.tasks.named("compileKotlinJs", Kotlin2JsCompile::class.java).get()
            // dependenciesTask remove all files from node_modules that are not defined in package.json
            dependsOn(compileTask, dependenciesTask)
            from(compileTask.outputFile)
            into("node_modules")
        }

        val compileTestJsTask = target.tasks.named("compileTestKotlinJs", Kotlin2JsCompile::class.java)

        target.tasks.register("runMocha", NodeTask::class.java) {
            dependsOn(dependenciesTask, compileTestJsTask, nodeModuleTask)
            setScript(project.file("node_modules/mocha/bin/mocha"))
            // NodeTask does not support lazy configuration through properties
            setArgs(listOf(compileTestJsTask.get().outputFile.relativeTo(project.projectDir)))
        }
    }

    private fun KotlinJsOptions.configure() {
        metaInfo = true
        sourceMap = true
        sourceMapEmbedSources = "always"
        moduleKind = "umd"
        main = "call"
    }

}
