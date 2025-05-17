package com.badoo.reaktive.compatibility

import kotlinx.validation.ApiValidationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class BinaryCompatibilityConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(plugin = "binary-compatibility-validator")
        target.extensions.configure(ApiValidationExtension::class) {
            nonPublicMarkers += "com.badoo.reaktive.utils.InternalReaktiveApi"

            if (target.hasProperty("check_publication")) {
                ignoredProjects.add("check-publication")
            } else {
                ignoredProjects.addAll(
                    listOf(
                        "benchmarks",
                        "jmh",
                        "sample-mpp-module",
                        "sample-android-app",
                        "sample-js-browser-app",
                        "sample-linuxx64-app",
                    )
                )
            }
        }
    }
}
