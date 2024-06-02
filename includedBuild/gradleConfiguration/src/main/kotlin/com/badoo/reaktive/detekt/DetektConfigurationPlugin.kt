package com.badoo.reaktive.detekt

import com.badoo.reaktive.getLibrary
import io.gitlab.arturbosch.detekt.CONFIGURATION_DETEKT_PLUGINS
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class DetektConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("io.gitlab.arturbosch.detekt")
        target.extensions.configure(DetektExtension::class.java) {
            source.setFrom(
                target.files(
                    target
                        .file("src")
                        .listFiles()
                        ?.filter { it.isDirectory && it.name.endsWith("main", ignoreCase = true) }
                )
            )

            config.setFrom(target.rootProject.files("detekt.yml"))
            buildUponDefaultConfig = true
        }
        target.dependencies.add(CONFIGURATION_DETEKT_PLUGINS, target.getLibrary("detekt-ktlint"))
    }
}
