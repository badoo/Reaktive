package com.badoo.reaktive.configuration

import com.android.build.gradle.BaseExtension
import com.badoo.reaktive.dependencies.Deps
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class MppConfigurationPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.extensions.create("configuration", MppConfigurationExtension::class.java, target)
        setupMultiplatformLibrary(target)
        setupAllTargetsWithDefaultSourceSets(target)
    }

    private fun setupMultiplatformLibrary(target: Project) {
        target.apply(plugin = "org.jetbrains.kotlin.multiplatform")
        target.group = target.findProperty("reaktive_group_id") as Any
        target.version = target.findProperty("reaktive_version") as Any
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            sourceSets {
                maybeCreate("commonMain").dependencies { implementation(Deps.kotlin.stdlib.common) }
                maybeCreate("commonTest").dependencies {
                    implementation(Deps.kotlin.test.common)
                    implementation(Deps.kotlin.test.annotationsCommon)
                }
            }
        }
    }

    private fun setupAllTargetsWithDefaultSourceSets(project: Project) {
        setupAndroidTarget(project)
        setupJvmTarget(project)
        setupJsTarget(project)
        setupLinuxX64Target(project)
        setupIosTargets(project)

        project.kotlin {
            sourceSets {
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
                maybeCreate("androidTest").dependsOn(getByName("jvmCommonTest"))

                maybeCreate("jsMain").dependsOn(getByName("jvmJsCommonMain"))
                maybeCreate("jsTest").dependsOn(getByName("jvmJsCommonTest"))

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

                maybeCreate("watchosX86Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("watchosX86Test").dependsOn(getByName("darwinCommonTest"))

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

                maybeCreate("iosArm32Main").dependsOn(getByName("darwinCommonMain"))
                maybeCreate("iosArm32Test").dependsOn(getByName("darwinCommonTest"))

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
                minSdkVersion(1)
                targetSdkVersion(29)
            }
        }
        project.kotlin {
            android {
                publishLibraryVariants("release", "debug")
                disableIfUndefined(Target.JVM)
            }
            sourceSets {
                maybeCreate("androidMain").dependencies { implementation(Deps.kotlin.stdlib) }
                maybeCreate("androidTest").dependencies { implementation(Deps.kotlin.test.junit) }
            }
        }
    }

    private fun setupJvmTarget(project: Project) {
        project.kotlin {
            jvm {
                disableIfUndefined(Target.JVM)
            }
            sourceSets {
                maybeCreate("jvmMain").dependencies { implementation(Deps.kotlin.stdlib) }
                maybeCreate("jvmTest").dependencies { implementation(Deps.kotlin.test.junit) }
            }
        }
    }

    private fun setupJsTarget(project: Project) {
        project.apply<JsPlugin>()
    }

    private fun setupLinuxX64Target(project: Project) {
        project.kotlin {
            linuxX64 {
                disableIfUndefined(Target.LINUX)
            }
        }
    }

    fun setupLinuxArm32HfpTarget(project: Project) {
        project.kotlin {
            linuxArm32Hfp {
                disableIfUndefined(Target.LINUX)
            }
            sourceSets {
                maybeCreate("linuxArm32HfpMain").dependsOn(getByName("linuxCommonMain"))
                maybeCreate("linuxArm32HfpTest").dependsOn(getByName("linuxCommonTest"))
            }
        }
    }

    private fun setupIosTargets(project: Project) {
        project.apply<DarwinPlugin>()
    }

    private fun Project.kotlin(action: Action<KotlinMultiplatformExtension>) {
        extensions.configure(KotlinMultiplatformExtension::class.java, action)
    }
}
