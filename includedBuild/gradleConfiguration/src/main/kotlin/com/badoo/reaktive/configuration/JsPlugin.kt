package com.badoo.reaktive.configuration

import com.android.utils.appendCapitalized
import com.badoo.reaktive.dependencies.Deps
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.jetbrains.kotlin.gradle.dsl.KotlinJsOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

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
                browser {
                    testTask {
                        useKarma {
                            useChromeHeadless()
                        }
                    }
                }
                nodejs {
                    testTask {
                        useCommonJs()
                    }
                }
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
        target.tasks.named(TASK_COMPILE_KOTLIN_JS, Kotlin2JsCompile::class.java) {
            kotlinOptions.configure()
        }
        target.tasks.named(TASK_COMPILE_KOTLIN_JS_TEST, Kotlin2JsCompile::class.java) {
            kotlinOptions.configure()
        }
    }

    private fun KotlinJsOptions.configure() {
        metaInfo = true
        sourceMap = true
        sourceMapEmbedSources = "always"
        moduleKind = "umd"
        main = "call"
    }

    companion object {
        private const val TASK_COMPILE_KOTLIN_JS = "compileKotlinJs"
        private const val TASK_COMPILE_KOTLIN_JS_TEST = "compileTestKotlinJs"

        const val TARGET_NAME_JS = "js"
    }
}
