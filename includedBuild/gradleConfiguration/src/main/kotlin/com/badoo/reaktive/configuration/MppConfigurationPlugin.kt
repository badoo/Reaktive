package com.badoo.reaktive.configuration

import com.android.build.gradle.BaseExtension
import com.badoo.reaktive.getLibrary
import org.gradle.api.Action
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class MppConfigurationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        setupMultiplatformLibrary(target)
        setupAllTargetsWithDefaultSourceSets(target)
    }

    private fun setupMultiplatformLibrary(target: Project) {
        target.apply(plugin = "org.jetbrains.kotlin.multiplatform")
        target.group = target.findProperty("reaktive_group_id") as Any
        target.version = target.findProperty("reaktive_version") as Any
        target.kotlin {
            sourceSets {
                maybeCreate("commonMain").dependencies {
                    implementation(target.getLibrary("kotlin-stdlib"))
                }
                maybeCreate("commonTest").dependencies {
                    implementation(target.getLibrary("kotlin-test"))
                }
            }

            targets.configureEach {
                compilations.configureEach {
                    compileTaskProvider.configure {
                        compilerOptions {
                            freeCompilerArgs.add("-Xexpect-actual-classes")
                        }
                    }
                }
            }
        }
    }

    private fun setupAllTargetsWithDefaultSourceSets(project: Project) {
        setupAndroidTarget(project)
        setupJvmTarget(project)
        setupJsTarget(project)
        setupWasmJsTarget(project)
        setupLinuxX64Target(project)
        setupIosTargets(project)

        project.kotlin {
            sourceSets {
                all {
                    languageSettings {
                        optIn("com.badoo.reaktive.utils.InternalReaktiveApi")
                    }
                }

                maybeCreate("jvmJsCommonMain").dependsOn(getByName("commonMain"))
                maybeCreate("jvmJsCommonTest").dependsOn(getByName("commonTest"))

                maybeCreate("jvmNativeCommonMain").dependsOn(getByName("commonMain"))
                maybeCreate("jvmNativeCommonTest").dependsOn(getByName("commonTest"))

                maybeCreate("jvmCommonMain").apply {
                    dependsOn(getByName("jvmJsCommonMain"))
                    dependsOn(getByName("jvmNativeCommonMain"))
                }
                maybeCreate("jvmCommonTest").apply {
                    dependsOn(getByName("jvmJsCommonTest"))
                    dependsOn(getByName("jvmNativeCommonTest"))
                }

                maybeCreate("jvmMain").dependsOn(getByName("jvmCommonMain"))
                maybeCreate("jvmTest").dependsOn(getByName("jvmCommonTest"))

                maybeCreate("androidMain").dependsOn(getByName("jvmCommonMain"))
                maybeCreate("androidUnitTest").dependsOn(getByName("jvmCommonTest"))

                maybeCreate("jsCommonMain").dependsOn(getByName("jvmJsCommonMain"))
                maybeCreate("jsCommonTest").dependsOn(getByName("jvmJsCommonTest"))

                maybeCreate("jsMain").dependsOn(getByName("jsCommonMain"))
                maybeCreate("jsTest").dependsOn(getByName("jsCommonTest"))

                maybeCreate("wasmJsMain").dependsOn(getByName("jsCommonMain"))
                maybeCreate("wasmJsTest").dependsOn(getByName("jsCommonTest"))

                maybeCreate("nativeCommonMain").dependsOn(getByName("jvmNativeCommonMain"))
                maybeCreate("nativeCommonTest").dependsOn(getByName("jvmNativeCommonTest"))

                maybeCreate("linuxCommonMain").dependsOn(getByName("nativeCommonMain"))
                maybeCreate("linuxCommonTest").dependsOn(getByName("nativeCommonTest"))

                maybeCreate("linuxX64Main").dependsOn(getByName("linuxCommonMain"))
                maybeCreate("linuxX64Test").dependsOn(getByName("linuxCommonTest"))

                maybeCreate("jvmNativeCommonMain").dependsOn(getByName("commonMain"))
                maybeCreate("jvmNativeCommonTest").dependsOn(getByName("commonTest"))

                maybeCreate("darwinCommonMain").dependsOn(getByName("nativeCommonMain"))
                maybeCreate("darwinCommonTest").dependsOn(getByName("nativeCommonTest"))

                maybeCreate("watchosArm32Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("watchosArm32Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("watchosArm64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("watchosArm64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("watchosX64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("watchosX64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("watchosSimulatorArm64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("watchosSimulatorArm64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("tvosArm64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("tvosArm64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("tvosX64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("tvosX64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("tvosSimulatorArm64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("tvosSimulatorArm64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("macosX64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("macosX64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("macosArm64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("macosArm64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("iosArm64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("iosArm64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("iosX64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("iosX64Test").dependsOn(getByName("darwinCommonTest"))

                maybeCreate("iosSimulatorArm64Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("iosSimulatorArm64Test").dependsOn(getByName("darwinCommonTest"))
            }
        }
    }

    private fun setupAndroidTarget(project: Project) {
        project.pluginManager.apply("com.android.library")
        project.extensions.configure(BaseExtension::class.java) {
            compileSdkVersion(29)
            defaultConfig {
                minSdk = 1
                targetSdk = 29
            }
            compileOptions {
                sourceCompatibility(JavaVersion.VERSION_1_8)
                targetCompatibility(JavaVersion.VERSION_1_8)
            }
        }
        project.tasks.withType<KotlinCompile> {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_1_8)
            }
        }
        project.kotlin {
            androidTarget {
                publishLibraryVariants("release", "debug")
            }
            sourceSets {
                maybeCreate("androidMain").dependencies { implementation(project.getLibrary("kotlin-stdlib")) }
                maybeCreate("androidUnitTest").dependencies { implementation(project.getLibrary("kotlin-test")) }
            }
        }
    }

    private fun setupJvmTarget(project: Project) {
        project.kotlin {
            jvm {
                compilations.getByName("main").apply {
                    compileTaskProvider.configure {
                        compilerOptions {
                            jvmTarget.set(JvmTarget.JVM_1_8)
                        }
                    }
                }
            }
            sourceSets {
                maybeCreate("jvmMain").dependencies { implementation(project.getLibrary("kotlin-stdlib")) }
                maybeCreate("jvmTest").dependencies { implementation(project.getLibrary("kotlin-test")) }
            }
        }
    }

    private fun setupJsTarget(project: Project) {
        project.kotlin {
            js {
                browser()
                nodejs()
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

    @OptIn(ExperimentalWasmDsl::class)
    private fun setupWasmJsTarget(project: Project) {
        project.kotlin {
            wasmJs {
                browser()
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

    private fun setupLinuxX64Target(project: Project) {
        project.kotlin {
            linuxX64 {
            }
        }
    }

    private fun setupIosTargets(project: Project) {
        project.kotlin {
            iosArm64()
            iosX64()
            iosSimulatorArm64()
            watchosArm32()
            watchosArm64()
            watchosX64()
            watchosSimulatorArm64()
            tvosArm64()
            tvosX64()
            tvosSimulatorArm64()
            macosX64()
            macosArm64()
        }
    }

    private fun Project.kotlin(action: Action<KotlinMultiplatformExtension>) {
        extensions.configure(KotlinMultiplatformExtension::class.java, action)
    }
}
