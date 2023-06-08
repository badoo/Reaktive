package com.badoo.reaktive.configuration

import org.gradle.api.Project
import javax.inject.Inject

open class MppConfigurationExtension @Inject constructor(
    private val project: Project
)
