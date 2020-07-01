package com.badoo.reaktive.configuration

import Target
import kotlinx.validation.ApiValidationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

class BinaryCompatibilityPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (Target.shouldDefineTarget(target, Target.ALL_LINUX_HOSTED)) {
            target.apply(plugin = "binary-compatibility-validator")
            target.extensions.configure(ApiValidationExtension::class) {
                ignoredProjects.addAll(
                    listOf(
                        "benchmarks",
                        "jmh",
                        "sample-mpp-module",
                        "sample-android-app",
                        "sample-js-browser-app",
                        "sample-linuxx64-app",
                        "sample-ios-app",
                        "sample-macos-app"
                    )
                )
            }
        }
    }
}
