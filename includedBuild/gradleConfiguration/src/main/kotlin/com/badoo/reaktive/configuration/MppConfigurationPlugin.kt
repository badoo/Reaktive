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
        if (Target.shouldDefineTarget(project, Target.ALL_LINUX_HOSTED)) {
            setupAndroidTarget(project)
            setupJvmTarget(project)
            setupJsTarget(project)
            setupLinuxX64Target(project)

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
                }
            }
        }

        if (Target.shouldDefineTarget(project, Target.ALL_MACOS_HOSTED)) {
            setupIosTargets(project)

            project.kotlin {
                sourceSets {
                    maybeCreate("jvmNativeCommonMain").dependsOn(getByName("commonMain"))
                    maybeCreate("jvmNativeCommonTest").dependsOn(getByName("commonTest"))

                    maybeCreate("nativeCommonMain").dependsOn(getByName("jvmNativeCommonMain"))
                    maybeCreate("nativeCommonTest").dependsOn(getByName("jvmNativeCommonTest"))

                    maybeCreate("darwinCommonMain").dependsOn(getByName("nativeCommonMain"))
                    maybeCreate("darwinCommonTest").dependsOn(getByName("nativeCommonTest"))

                    maybeCreate("watchosArm32Main").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("watchosArm32Test").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("watchosArm64Main").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("watchosArm32Test").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("watchosArm32Main").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("watchosArm64Test").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("watchosSimMain").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("watchosSimTest").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("tvosArm64Main").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("tvosArm64Test").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("tvosSimMain").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("tvosSimTest").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("macosX64Main").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("macosX64Test").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("ios32Main").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("ios32Test").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("ios64Main").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("ios64Test").dependsOn(getByName("darwinCommonTest"))

                    maybeCreate("iosSimMain").dependsOn(getByName("darwinCommonMain"))
                    maybeCreate("iosSimTest").dependsOn(getByName("darwinCommonTest"))
                }
            }
        }
    }

    private fun setupAndroidTarget(project: Project) {
        if (!Target.shouldDefineTarget(project, Target.JVM)) return
        project.pluginManager.apply("com.android.library")
        project.extensions.configure(BaseExtension::class.java) {
            buildToolsVersion("29.0.2")
            compileSdkVersion(29)
            defaultConfig {
                minSdkVersion(1)
                targetSdkVersion(29)
            }
        }
        project.kotlin {
            android {
                publishLibraryVariants("release", "debug")
            }
            sourceSets {
                maybeCreate("androidMain").dependencies { implementation(Deps.kotlin.stdlib) }
                maybeCreate("androidTest").dependencies { implementation(Deps.kotlin.test.junit) }
            }
        }
    }

    private fun setupJvmTarget(project: Project) {
        if (!Target.shouldDefineTarget(project, Target.JVM)) return
        project.kotlin {
            jvm()
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
        if (!Target.shouldDefineTarget(project, Target.LINUX)) return
        project.kotlin {
            linuxX64()
        }
    }

    fun setupLinuxArm32HfpTarget(project: Project) {
        if (!Target.shouldDefineTarget(project, Target.LINUX)) return
        project.kotlin {
            linuxArm32Hfp()
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
