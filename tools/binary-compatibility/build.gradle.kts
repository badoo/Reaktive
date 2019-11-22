plugins {
    `kotlin-dsl`
    kotlin("jvm")
}

dependencies {
    implementation(Deps.asm)
    implementation(Deps.asm.tree)
    implementation(Deps.kotlinx.metadata.jvm)

    testImplementation(Deps.kotlin.test.junit)
}

tasks.named("test", Test::class) {
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

    systemProperty("overwrite.output", "false")
    systemProperty("kotlinVersion", rootProject.ext["reaktive_version"].toString())
    systemProperty("testCasesClassesDirs", sourceSets.test.get().output.classesDirs.asPath)
    jvmArgs("-ea")
}
