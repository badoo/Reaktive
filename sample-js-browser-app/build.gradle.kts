/*
 * Use the following Gradle tasks to run your application:
 * :jsBrowserProductionRun - release mode JS
 * :jsBrowserDevelopmentRun - debug mode JS
 * :wasmJsBrowserProductionRun - release mode WASM-JS
 * :wasmJsBrowserDevelopmentRun - debug mode WASM-JS
 */
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("kotlin-multiplatform")
}

kotlin {
    js {
        browser()
        binaries.executable()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":reaktive"))
            implementation(project(":sample-mpp-module"))
        }
    }
}
