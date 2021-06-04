package com.badoo.reaktive.configuration

import com.android.utils.appendCapitalized
import com.badoo.reaktive.dependencies.Deps
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("UnstableApiUsage")
class JsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureJsCompilation(target)
    }

    private fun configureJsCompilation(target: Project) {
        if (!Target.shouldDefineTarget(target, Target.JS)) {
            return
        }
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js(TARGET_NAME_JS) {
                browser()
                nodejs()

                binaries.library()
            }
            sourceSets.getByName(TARGET_NAME_JS.appendCapitalized(SourceSet.MAIN_SOURCE_SET_NAME)) {
                dependencies {
                    implementation(Deps.kotlin.stdlib.js)
                }
            }
            sourceSets.getByName(TARGET_NAME_JS.appendCapitalized(SourceSet.TEST_SOURCE_SET_NAME)) {
                dependencies {
                    implementation(Deps.kotlin.test.js)
                }
            }
        }
    }

    companion object {
        const val TARGET_NAME_JS = "js"
    }
}
