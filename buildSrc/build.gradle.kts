plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation(Deps.kotlin.plugin)
    implementation(Deps.android.plugin)
    implementation(Deps.bintray)
    implementation(Deps.detekt)
}

kotlin {
    // Add Deps to compilation, so it will become available in main project
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}
