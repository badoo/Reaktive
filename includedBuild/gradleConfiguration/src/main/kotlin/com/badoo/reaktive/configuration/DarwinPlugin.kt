package com.badoo.reaktive.configuration

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.tasks.SourceSet
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask
import org.jetbrains.kotlin.konan.target.KonanTarget

@Suppress("UnstableApiUsage")
class DarwinPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureDarwinCompilation(target)
    }

    private fun configureDarwinCompilation(target: Project) {
        val buildBinariesTasks = mutableListOf<Any>()
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            if (Target.shouldDefineTarget(target, Target.IOS)) {
                iosArm32(TARGET_NAME_IOS_ARM32).configureFramework(buildBinariesTasks)
                iosArm64(TARGET_NAME_IOS_ARM64).configureFramework(buildBinariesTasks)
                iosX64(TARGET_NAME_IOS_X64).configureFramework(buildBinariesTasks)
            }
            if (Target.shouldDefineTarget(target, Target.WATCHOS)) {
                watchosArm32(TARGET_NAME_WATCHOS_ARM32).configureFramework(buildBinariesTasks)
                watchosArm64(TARGET_NAME_WATCHOS_ARM64).configureFramework(buildBinariesTasks)
                watchosX86(TARGET_NAME_WATCHOS_SIM).configureFramework(buildBinariesTasks)
            }
            if (Target.shouldDefineTarget(target, Target.TVOS)) {
                tvosArm64(TARGET_NAME_TVOS_ARM64).configureFramework(buildBinariesTasks)
                tvosX64(TARGET_NAME_TVOS_X64).configureFramework(buildBinariesTasks)
            }
            if (Target.shouldDefineTarget(target, Target.MACOS)) {
                macosX64(TARGET_NAME_MACOS_X64).configureFramework(buildBinariesTasks)
            }
        }
        setupBuildAll(target, buildBinariesTasks)
        setupBuildFat(target)
    }

    private fun KotlinNativeTarget.configureFramework(buildTasks: MutableCollection<Any>) {
        binaries { framework() }
        buildTasks += compilations.named(SourceSet.MAIN_SOURCE_SET_NAME).map { it.binariesTaskName }
    }

    private fun setupBuildAll(
        target: Project,
        buildTasks: Collection<Any>
    ) {
        val allBinariesTaskProvider = target.tasks.register(TASK_NAME_IOS_BINARIES) {
            dependsOn(buildTasks)
        }
        target.tasks.named(LifecycleBasePlugin.BUILD_TASK_NAME) {
            dependsOn(allBinariesTaskProvider)
        }
    }

    private fun setupBuildFat(target: Project) {
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            targets.withType(KotlinNativeTarget::class.java).configureEach {
                if (konanTarget == KonanTarget.MACOS_X64) {
                    // macOS does not support fat framework
                    return@configureEach
                }
                binaries.withType(Framework::class.java).configureEach {
                    val framework = this
                    val binaryBaseName = baseName.takeIf { it != target.name } ?: ""
                    val buildType = buildType.name.toLowerCase()
                    val family = konanTarget.family.toString().toLowerCase()
                    val taskName = "$TASK_NAME_FAT${binaryBaseName.capitalize()}${family.capitalize()}${buildType.capitalize()}"
                    // get or create fatBaseNameFamilyBuildType task
                    val provider =
                        try {
                            target.tasks.named(taskName, FatFrameworkTask::class.java)
                        } catch (unknownException: UnknownDomainObjectException) {
                            target.tasks.register(taskName, FatFrameworkTask::class.java) {
                                baseName = framework.baseName
                                // Skip base name, if it was not changed
                                // e.g. fatDebug, fatCustomBaseNameDebug
                                val binaryNameFolder = if (binaryBaseName.isNotEmpty()) "$binaryBaseName/" else ""
                                destinationDir = project.file(
                                    "${project.buildDir}/fat-framework/$binaryNameFolder$family/$buildType"
                                )
                            }
                        }
                    // Add current framework to fat output
                    provider.configure { from(framework) }
                }
            }
        }
    }

    companion object {
        const val TARGET_NAME_IOS_ARM32 = "ios32"
        const val TARGET_NAME_IOS_ARM64 = "ios64"
        const val TARGET_NAME_IOS_X64 = "iosSim"
        const val TARGET_NAME_WATCHOS_ARM32 = "watchosArm32"
        const val TARGET_NAME_WATCHOS_ARM64 = "watchosArm64"
        const val TARGET_NAME_WATCHOS_SIM = "watchosSim"
        const val TARGET_NAME_TVOS_ARM64 = "tvosArm64"
        const val TARGET_NAME_TVOS_X64 = "tvosSim"
        const val TARGET_NAME_MACOS_X64 = "macosX64"

        const val TASK_NAME_IOS_BINARIES = "iosBinaries"
        const val TASK_NAME_FAT = "fat"
    }
}
