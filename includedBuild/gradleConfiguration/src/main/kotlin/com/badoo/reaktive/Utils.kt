package com.badoo.reaktive

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.getByType

/**
 * @param alias from `libs.versions.toml`.
 */
internal fun Project.getLibrary(alias: String): Provider<MinimalExternalModuleDependency> =
    extensions
        .getByType<VersionCatalogsExtension>()
        .named("libs")
        .findLibrary(alias)
        .get()
