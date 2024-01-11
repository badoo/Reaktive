package com.badoo.reaktive.configuration

import com.badoo.reaktive.getLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

@OptIn(ExperimentalWasmDsl::class)
class WasmJsPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureWasmJsCompilation(target)
    }

    private fun configureWasmJsCompilation(target: Project) {
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            wasmJs {
                browser()
                disableIfUndefined(Target.WASM_JS)
            }
            sourceSets.getByName("wasmJsMain") {
                dependencies {
                    implementation(project.getLibrary("kotlin-stdlib"))
                }
            }
            sourceSets.getByName("wasmJsTest") {
                dependencies {
                    implementation(project.getLibrary("kotlin-test"))
                }
            }
        }
    }
}
