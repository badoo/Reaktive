plugins {
    kotlin("jvm") version ("1.4-M3")
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
    gradlePluginPortal()
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
}

dependencies {
    implementation(Deps.kotlin.plugin)
    implementation(Deps.kotlin.compilerEmbeddable)
    implementation(Deps.android.plugin)
    implementation(Deps.jmh.plugin)
    implementation(Deps.bintray)
    implementation(Deps.detekt.plugin)
    implementation(Deps.shadow)
}

kotlin {
    // Add Deps to compilation, so it will become available in main project
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}
