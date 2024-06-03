package com.badoo.reaktive.configuration

import com.badoo.reaktive.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class JsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureJsCompilation(target)
    }

    private fun configureJsCompilation(target: Project) {
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            js {
                browser()
                nodejs()

                disableIfUndefined(Target.JS)
            }
            sourceSets.getByName("jsMain") {
                dependencies {
                    implementation(project.getLibrary("kotlin-stdlib"))
                }
            }
            sourceSets.getByName("jsTest") {
                dependencies {
                    implementation(project.getLibrary("kotlin-test"))
                }
            }
        }
    }
}
