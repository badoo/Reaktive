/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.tools.tests

import org.jetbrains.kotlin.tools.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import java.io.File
import java.util.jar.JarFile

class RuntimePublicAPITest {

    @[Rule JvmField]
    val testName = TestName()

    @Test fun coroutinesInterop() {
        snapshotAPIAndCompare("../../coroutines-interop/build/libs", "coroutines-interop-jvm", listOf("com.badoo.reaktive"))
    }

    @Test fun reaktive() {
        snapshotAPIAndCompare("../../reaktive/build/libs", "reaktive-jvm")
    }

    @Test fun reaktiveAnnotations() {
        snapshotAPIAndCompare("../../reaktive-annotations/build/libs", "reaktive-annotations-jvm")
    }

    @Test fun reaktiveTesting() {
        snapshotAPIAndCompare("../../reaktive-testing/build/libs", "reaktive-testing-jvm")
    }

    @Test fun rxjava2Interop() {
        snapshotAPIAndCompare("../../rxjava2-interop/build/libs", "rxjava2-interop")
    }

    @Test fun rxjava3Interop() {
        snapshotAPIAndCompare("../../rxjava3-interop/build/libs", "rxjava3-interop")
    }

    @Test fun utils() {
        snapshotAPIAndCompare("../../utils/build/libs", "utils-jvm")
    }

    private fun snapshotAPIAndCompare(
        basePath: String,
        jarPattern: String,
        publicPackages: List<String> = emptyList(),
        nonPublicPackages: List<String> = emptyList()
    ) {
        val base = File(basePath).absoluteFile.normalize()
        val jarFile = getJarPath(base, jarPattern, System.getProperty("kotlinVersion"))

        val publicPackagePrefixes = publicPackages.map { it.replace('.', '/') + '/' }
        val publicPackageFilter = { className: String -> publicPackagePrefixes.none { className.startsWith(it) } }

        println("Reading binary API from $jarFile")
        val api = getBinaryAPI(JarFile(jarFile), publicPackageFilter).filterOutNonPublic(nonPublicPackages)

        val target = File("reference-public-api")
            .resolve(testName.methodName.replaceCamelCaseWithDashedLowerCase() + ".txt")

        api.dumpAndCompareWith(target)
    }

    private fun getJarPath(base: File, jarPattern: String, kotlinVersion: String?): File {
        val versionPattern = kotlinVersion?.let { "-" + Regex.escape(it) } ?: ".+"
        val regex = Regex(jarPattern + versionPattern + "\\.jar")
        val files = (base.listFiles() ?: throw Exception("Cannot list files in $base"))
            .filter { it.name.let {
                    it matches regex
                            && !it.endsWith("-sources.jar")
                            && !it.endsWith("-javadoc.jar") } }

        return files.singleOrNull() ?: throw Exception("No single file matching $regex in $base:\n${files.joinToString("\n")}")
    }

}

