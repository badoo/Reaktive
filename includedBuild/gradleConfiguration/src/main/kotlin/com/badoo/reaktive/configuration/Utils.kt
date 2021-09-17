package com.badoo.reaktive.configuration

import com.badoo.reaktive.configuration.Target.Companion.shouldDefineTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

fun KotlinTarget.disableIfUndefined(target: Target) {
    if (!shouldDefineTarget(project, target)) {
        compilations.all {
            compileKotlinTask.enabled = false
        }

        if (target == Target.LINUX) {
            project.afterEvaluate {
                tasks
                    .filter { it.name.startsWith("cinterop") }
                    .forEach { it.enabled = false }
            }
        }
    }
}
