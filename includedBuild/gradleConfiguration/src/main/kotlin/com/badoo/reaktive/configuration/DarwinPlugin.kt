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
            iosArm32().disableIfUndefined(Target.IOS)
            iosArm64().disableIfUndefined(Target.IOS)
            iosX64().disableIfUndefined(Target.IOS)
            iosSimulatorArm64().disableIfUndefined(Target.IOS)
            watchosArm32().disableIfUndefined(Target.WATCHOS)
            watchosArm64().disableIfUndefined(Target.WATCHOS)
            watchosX86().disableIfUndefined(Target.WATCHOS)
            watchosX64().disableIfUndefined(Target.WATCHOS)
            watchosSimulatorArm64().disableIfUndefined(Target.WATCHOS)
            tvosArm64().disableIfUndefined(Target.TVOS)
            tvosX64().disableIfUndefined(Target.TVOS)
            tvosSimulatorArm64().disableIfUndefined(Target.TVOS)
            macosX64().disableIfUndefined(Target.MACOS)
            macosArm64().disableIfUndefined(Target.MACOS)
        }
    }
}
