package com.badoo.reaktive.publish

import com.badoo.reaktive.configuration.Target
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.api.publish.maven.tasks.PublishToMavenLocal
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin
import java.net.URI

class PublishConfigurationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply(MavenPublishPlugin::class.java)
        val taskConfigurationMap = createConfigurationMap(target)
        disablePublishingTasks(target, taskConfigurationMap)
        createFilteredPublishTasks(target)
        setupPublishing(target)
        setupSign(target)
        createEmptySourcesJar(target)
    }

    private fun setupPublishing(project: Project) {
        val publishing = project.extensions.getByType(PublishingExtension::class.java)
        publishing.repositories {
            maven {
                name = "sonatype"
                val repositoryId = project.findProperty("sonatype.repository")
                url = URI.create(
                    if (repositoryId != null) {
                        "https://oss.sonatype.org/service/local/staging/deployByRepositoryId/$repositoryId/"
                    } else {
                        "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                    }
                )
                credentials {
                    username = project.findProperty("sonatype.username").toString()
                    password = project.findProperty("sonatype.password").toString()
                }
            }
        }
        publishing.publications.withType(MavenPublication::class.java).configureEach {
            pom {
                name.set(project.name)
                description.set("Kotlin multi-platform implementation of Reactive Extensions")
                url.set("https://github.com/badoo/Reaktive")

                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://github.com/badoo/Reaktive/blob/master/LICENSE")
                    }
                }

                developers {
                    developer {
                        id.set("arkivanov")
                        name.set("Arkadii Ivanov")
                        email.set("oss.sonatype@team.bumble.com")
                    }
                }

                scm {
                    connection.set("scm:git:ssh://github.com/badoo/Reaktive.git")
                    developerConnection.set("scm:git:ssh://github.com/badoo/Reaktive.git")
                    url.set("https://github.com/badoo/Reaktive")
                }
            }
        }
    }

    private fun setupSign(project: Project) {
        val inMemoryKey = System.getenv("SIGNING_KEY") ?: project.findProperty("signing.key")?.toString()
        // Constants from PgpSignatoryFactory.PROPERTIES
        val password = project.findProperty("signing.password")?.toString()
        val keyFile = project.findProperty("signing.secretKeyRingFile")?.toString()
        val keyId = project.findProperty("signing.keyId")?.toString()

        if (inMemoryKey == null && keyFile == null) {
            project.logger.warn("No signing config provided, skip signing")
            return
        }
        project.plugins.apply(SigningPlugin::class.java)
        project.extensions.getByType(SigningExtension::class.java).apply {
            isRequired = true
            sign(
                project.extensions.getByType(PublishingExtension::class.java).publications
            )
            if (inMemoryKey != null) {
                useInMemoryPgpKeys(inMemoryKey, password)
            }
        }
    }

    private fun createFilteredPublishTasks(project: Project) {
        // Create umbrella task with enabled publish tasks only.
        // We can't use regular publishToMavenLocal/publishAll
        // because even if task is disabled all its dependencies will be still executed.
        project.tasks.register(TASK_FILTERED_PUBLISH_TO_MAVEN_LOCAL) {
            group = PublishingPlugin.PUBLISH_TASK_GROUP
            dependsOn(
                project
                    .tasks
                    .withType(PublishToMavenLocal::class.java)
                    .matching { it.enabled }
            )
        }
        project.tasks.register(TASK_FILTERED_PUBLISH_TO_SONATYPE) {
            group = PublishingPlugin.PUBLISH_TASK_GROUP
            dependsOn(
                project
                    .tasks
                    .withType(PublishToMavenRepository::class.java)
                    .matching { it.enabled && it.repository.name == "sonatype" }
            )
        }
    }

    private fun createConfigurationMap(project: Project): Map<String, Boolean> {
        return mapOf(
            "kotlinMultiplatform" to Target.shouldPublishTarget(project, Target.META),
            KotlinMultiplatformPlugin.METADATA_TARGET_NAME to Target.shouldPublishTarget(project, Target.META),
            "jvm" to Target.shouldPublishTarget(project, Target.JVM),
            "js" to Target.shouldPublishTarget(project, Target.JS),
            "androidDebug" to Target.shouldPublishTarget(project, Target.JVM),
            "androidRelease" to Target.shouldPublishTarget(project, Target.JVM),
            "linuxX64" to Target.shouldPublishTarget(project, Target.LINUX),
            "linuxArm32Hfp" to Target.shouldPublishTarget(project, Target.LINUX),
            "iosArm32" to Target.shouldPublishTarget(project, Target.IOS),
            "iosArm64" to Target.shouldPublishTarget(project, Target.IOS),
            "iosX64" to Target.shouldPublishTarget(project, Target.IOS),
            "iosSimulatorArm64" to Target.shouldPublishTarget(project, Target.IOS),
            "watchosArm32" to Target.shouldPublishTarget(project, Target.WATCHOS),
            "watchosArm64" to Target.shouldPublishTarget(project, Target.WATCHOS),
            "watchosX86" to Target.shouldPublishTarget(project, Target.WATCHOS),
            "watchosX64" to Target.shouldPublishTarget(project, Target.WATCHOS),
            "watchosSimulatorArm64" to Target.shouldPublishTarget(project, Target.WATCHOS),
            "tvosArm64" to Target.shouldPublishTarget(project, Target.TVOS),
            "tvosX64" to Target.shouldPublishTarget(project, Target.TVOS),
            "tvosSimulatorArm64" to Target.shouldPublishTarget(project, Target.TVOS),
            "macosX64" to Target.shouldPublishTarget(project, Target.MACOS),
            "macosArm64" to Target.shouldPublishTarget(project, Target.MACOS)
        )
    }

    private fun disablePublishingTasks(
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

    private fun createEmptySourcesJar(project: Project) {
        val task = project.tasks.register("javadocJar", Jar::class.java) {
            archiveClassifier.set("javadoc")
        }
        project
            .extensions
            .getByType(PublishingExtension::class.java)
            .publications
            .withType(MavenPublication::class.java)
            .configureEach { artifact(task.get()) }
    }

    companion object {
        const val TASK_FILTERED_PUBLISH_TO_MAVEN_LOCAL = "publishAllFilteredToMavenLocal"
        const val TASK_FILTERED_PUBLISH_TO_SONATYPE = "publishAllFilteredToSonatype"
    }
}
