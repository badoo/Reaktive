package com.badoo.reaktive.publish

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

class PublishConfigurationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.plugins.apply(MavenPublishPlugin::class.java)
        target.plugins.apply("org.jetbrains.dokka")
        target.pluginManager.apply("com.vanniktech.maven.publish")
        target.extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
            configureBasedOnAppliedPlugins()
            publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
            if (target.findProperty("signingInMemoryKey") != null) {
                signAllPublications()
            }
            coordinates(
                artifactId = target.name,
                groupId = target.findProperty("reaktive_group_id").toString(),
                version = target.findProperty("reaktive_version").toString(),
            )
            pom { setup(target.name) }
        }
    }

    private fun MavenPom.setup(projectName: String) {
        name.set(projectName)
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
