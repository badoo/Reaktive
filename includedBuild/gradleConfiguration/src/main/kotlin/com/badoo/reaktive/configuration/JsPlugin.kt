package com.badoo.reaktive.configuration

import com.badoo.reaktive.dependencies.Deps
import org.gradle.api.Plugin
import org.gradle.api.Project
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
            js {
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
            sourceSets.getByName("jsMain") {
                dependencies {
                    implementation(Deps.kotlin.stdlib.js)
                }
            }
            sourceSets.getByName("jsTest") {
                dependencies {
                    implementation(Deps.kotlin.test.js)
                }
            }
        }
    }
}
