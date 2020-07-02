package com.badoo.reaktive.configuration

import org.gradle.api.Project
import javax.inject.Inject

open class MppConfigurationExtension @Inject constructor(
    private val project: Project
) {
    var isLinuxArm32HfpEnabled: Boolean = false
        private set

    fun enableLinuxArm32Hfp() {
        if (isLinuxArm32HfpEnabled) return
        project.plugins.findPlugin(MppConfigurationPlugin::class.java)?.setupLinuxArm32HfpTarget(project)
        isLinuxArm32HfpEnabled = true
    }
}
