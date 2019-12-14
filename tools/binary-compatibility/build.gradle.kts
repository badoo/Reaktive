plugins {
    kotlin
}

dependencies {
    implementation(Deps.asm)
    implementation(Deps.asm.tree)
    implementation(Deps.kotlinx.metadata.jvm)

    testImplementation(Deps.kotlin.test.junit)
}

tasks.named("test", Test::class) {

    // Build all jars, sync this list with RuntimePublicAPITest
    dependsOn(
        ":coroutines-interop:jvmJar",
        ":reaktive-annotations:jvmJar",
        ":reaktive:jvmJar",
        ":reaktive-annotations:jvmJar",
        ":reaktive-testing:jvmJar",
        ":rxjava2-interop:jar",
        ":rxjava3-interop:jar",
        ":utils:jvmJar"
    )

    // Re-run this task always, caching is broken because of writing files by test
    outputs.upToDateWhen { false }

    systemProperty("overwrite.output", findProperty("binary-compatibility-override") ?: "true")
    systemProperty("kotlinVersion", findProperty("reaktive_version").toString())
    systemProperty("testCasesClassesDirs", sourceSets.test.get().output.classesDirs.asPath)
    jvmArgs("-ea")
}
