package com.badoo.reaktive.configuration

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("UnstableApiUsage")
class DarwinPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        configureDarwinCompilation(target)
    }

    private fun configureDarwinCompilation(target: Project) {
        target.extensions.configure(KotlinMultiplatformExtension::class.java) {
            if (Target.shouldDefineTarget(target, Target.IOS)) {
                iosArm32()
                iosArm64()
                iosX64()
                iosSimulatorArm64()
            }
            if (Target.shouldDefineTarget(target, Target.WATCHOS)) {
                watchosArm32()
                watchosArm64()
                watchosX86()
                watchosX64()
                watchosSimulatorArm64()
            }
            if (Target.shouldDefineTarget(target, Target.TVOS)) {
                tvosArm64()
                tvosX64()
                tvosSimulatorArm64()
            }
            if (Target.shouldDefineTarget(target, Target.MACOS)) {
                macosX64()
                macosArm64()
            }
        }
    }
}
