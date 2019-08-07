import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.BintrayPlugin
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.internal.artifact.FileBasedMavenArtifact
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

abstract class PublishPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        setupPublishingForMultiPlatform(target)
        setupBintrayPublishingInformation(target)
        setupBintrayTaskForKotlinMultiPlatform(target)
    }

    private fun setupPublishingForMultiPlatform(target: Project) {
        val mppTarget = Target.currentTarget()
        val taskConfigurationMap = mapOf(
            "kotlinMultiplatform" to mppTarget.meta,
            "metadata" to mppTarget.meta,
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
        target.extensions.findByType(KotlinMultiplatformExtension::class)?.apply {
            targets.configureEach { configureMavenPublicationTasksForEnvironment(this, taskConfigurationMap) }
        }
    }

    private fun configureMavenPublicationTasksForEnvironment(
        kotlinTarget: KotlinTarget,
        taskConfigurationMap: Map<String, Boolean>
    ) {
        kotlinTarget.mavenPublication {
            kotlinTarget.project.tasks.withType(AbstractPublishToMaven::class).configureEach {
                onlyIf {
                    taskConfigurationMap[publication.name] == true
                }
            }
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

    private fun setupBintrayTaskForKotlinMultiPlatform(target: Project) {
        target.tasks.named(BintrayUploadTask.getTASK_NAME(), BintrayUploadTask::class) {
            dependsOn(project.tasks.named(MavenPublishPlugin.PUBLISH_LOCAL_LIFECYCLE_TASK_NAME))
            doFirst {
                val publishing = project.extensions.getByType(PublishingExtension::class)
                // https://github.com/bintray/gradle-bintray-plugin/issues/229
                publishing.publications
                    .filterIsInstance<MavenPublication>()
                    .forEach { publication ->
                        val moduleFile = project.buildDir.resolve("publications/${publication.name}/module.json")
                        if (moduleFile.exists()) {
                            publication.artifact(object : FileBasedMavenArtifact(moduleFile) {
                                override fun getDefaultExtension() = "module"
                            })
                        }
                    }
                // https://github.com/bintray/gradle-bintray-plugin/issues/256
                val publications = publishing.publications
                    .filterIsInstance<MavenPublication>()
                    .map {
                        logger.warn("Uploading artifact '${it.groupId}:${it.artifactId}:${it.version}' from publication '${it.name}'")
                        it.name
                    }
                    .toTypedArray()
                setPublications(*publications)
            }
        }
    }
}