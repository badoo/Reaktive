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
        val taskConfigurationMap = createConfigurationMap(target)
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

    private fun createConfigurationMap(project: Project): Map<String, Boolean> {
        return mapOf(
            "kotlinMultiplatform" to Target.shouldPublishTarget(project, Target.META),
            KotlinMultiplatformPlugin.METADATA_TARGET_NAME to Target.shouldPublishTarget(project, Target.META),
            "jvm" to Target.shouldPublishTarget(project, Target.JVM),
            JsPlugin.TARGET_NAME_JS to Target.shouldPublishTarget(project, Target.JS),
            "androidDebug" to Target.shouldPublishTarget(project, Target.JVM),
            "androidRelease" to Target.shouldPublishTarget(project, Target.JVM),
            "linuxX64" to Target.shouldPublishTarget(project, Target.LINUX),
            "linuxArm32Hfp" to Target.shouldPublishTarget(project, Target.LINUX),
            DarwinPlugin.TARGET_NAME_IOS_ARM32 to Target.shouldPublishTarget(project, Target.IOS),
            DarwinPlugin.TARGET_NAME_IOS_ARM64 to Target.shouldPublishTarget(project, Target.IOS),
            DarwinPlugin.TARGET_NAME_IOS_X64 to Target.shouldPublishTarget(project, Target.IOS),
            DarwinPlugin.TARGET_NAME_WATCHOS_ARM32 to Target.shouldPublishTarget(project, Target.WATCHOS),
            DarwinPlugin.TARGET_NAME_WATCHOS_ARM64 to Target.shouldPublishTarget(project, Target.WATCHOS),
            DarwinPlugin.TARGET_NAME_WATCHOS_SIM to Target.shouldPublishTarget(project, Target.WATCHOS),
            DarwinPlugin.TARGET_NAME_TVOS_ARM64 to Target.shouldPublishTarget(project, Target.TVOS),
            DarwinPlugin.TARGET_NAME_TVOS_X64 to Target.shouldPublishTarget(project, Target.TVOS),
            DarwinPlugin.TARGET_NAME_MACOS_X64 to Target.shouldPublishTarget(project, Target.MACOS)
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
                logger.warn("Publication configuration map: $taskConfigurationMap")
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
