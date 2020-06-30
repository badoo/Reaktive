package com.badoo.reaktive.configuration

import org.gradle.api.Project
import javax.inject.Inject

open class ConfigurationExtension @Inject constructor(
    private val project: Project
) {
    var isLinuxArm32HfpEnabled: Boolean = false
        private set

    fun enableLinuxArm32Hfp() {
        if (isLinuxArm32HfpEnabled) return
        project.plugins.findPlugin(ConfigurationPlugin::class.java)?.setupLinuxArm32HfpTarget(project)
        isLinuxArm32HfpEnabled = true
    }
}
