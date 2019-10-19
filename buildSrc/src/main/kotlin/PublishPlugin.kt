import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

abstract class PublishPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val taskConfigurationMap = createConfigurationMap()
        createFilteredPublishToMavenLocalTask(target)
        setupLocalPublishing(target, taskConfigurationMap)
        setupBintrayPublishingInformation(target)
        setupBintrayPublishing(target, taskConfigurationMap)
    }

    private fun createFilteredPublishToMavenLocalTask(target: Project) {
        // Create empty task with enabled publish tasks only
        // We can't use regular publishToMavenLocal
        target.tasks.register(TASK_FILTERED_PUBLISH_TO_MAVEN_LOCAL) {
            dependsOn(project.tasks.matching { it is AbstractPublishToMaven && it.enabled })
        }
    }

    private fun createConfigurationMap(): Map<String, Boolean> {
        val mppTarget = Target.currentTarget()
        return mapOf(
            "kotlinMultiplatform" to mppTarget.meta,
            KotlinMultiplatformPlugin.METADATA_TARGET_NAME to mppTarget.meta,
            "jvm" to mppTarget.common,
            JsPlugin.TARGET_NAME_JS to mppTarget.common,
            "androidDebug" to mppTarget.common,
            "androidRelease" to mppTarget.common,
            "linuxX64" to mppTarget.common,
            "linuxArm32Hfp" to mppTarget.common,
            IosPlugin.TARGET_NAME_X32 to mppTarget.ios,
            IosPlugin.TARGET_NAME_X64 to mppTarget.ios,
            IosPlugin.TARGET_NAME_SIM to mppTarget.ios
        )
    }

    private fun setupLocalPublishing(
        target: Project,
        taskConfigurationMap: Map<String, Boolean>
    ) {
        target.project.tasks.withType(AbstractPublishToMaven::class).configureEach {
            val configuration = publication?.name ?: run {
                // Android Plugin does not set publication property after creation of task
                logger.warn("Unable to configure task $name in place, using hacks instead")
                val configuration = taskConfigurationMap.keys
                    .find { name.contains(it, ignoreCase = true) }
                logger.warn("Found $configuration for $name")
                configuration
            }
            enabled = taskConfigurationMap[configuration] == true
        }
    }

    private fun setupBintrayPublishingInformation(target: Project) {
        target.plugins.apply(BintrayPlugin::class)
        target.extensions.getByType(BintrayExtension::class).apply {
            user = target.findProperty("bintray_user")?.toString()
            key = target.findProperty("bintray_key")?.toString()
            pkg.apply {
                repo = "maven"
                name = "reaktive"
                userOrg = "badoo"
                vcsUrl = "https://github.com/badoo/Reaktive.git"
                setLicenses("Apache-2.0")
                version.name = target.property("reaktive_version")?.toString()
            }
        }
    }

    private fun setupBintrayPublishing(
        target: Project,
        taskConfigurationMap: Map<String, Boolean>
    ) {
        target.tasks.named(BintrayUploadTask.getTASK_NAME(), BintrayUploadTask::class) {
            dependsOn(project.tasks.named(TASK_FILTERED_PUBLISH_TO_MAVEN_LOCAL))
            doFirst {
                val publishing = project.extensions.getByType(PublishingExtension::class)
                // https://github.com/bintray/gradle-bintray-plugin/issues/229
                publishing.publications
                    .filterIsInstance<MavenPublication>()
                    .forEach { publication ->
                        val moduleFile = project.buildDir
                            .resolve("publications/${publication.name}/module.json")
                        if (moduleFile.exists()) {
                            publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
                                override fun getDefaultExtension() = "module"
                            })
                        }
                    }
                // https://github.com/bintray/gradle-bintray-plugin/issues/256
                val publications = publishing.publications
                    .filterIsInstance<MavenPublication>()
                    .filter {
                        val res = taskConfigurationMap[it.name] == true
                        logger.warn("Artifact '${it.groupId}:${it.artifactId}:${it.version}' from publication '${it.name}' should be published: $res")
                        res
                    }
                    .map {
                        logger.warn("Uploading artifact '${it.groupId}:${it.artifactId}:${it.version}' from publication '${it.name}'")
                        it.name
                    }
                    .toTypedArray()
                setPublications(*publications)
            }
        }
    }

    companion object {
        const val TASK_FILTERED_PUBLISH_TO_MAVEN_LOCAL = "filteredPublishToMavenLocal"
    }
}
