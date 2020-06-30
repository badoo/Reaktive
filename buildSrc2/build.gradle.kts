plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    jcenter()
    gradlePluginPortal()
}

dependencies {
    implementation(Deps.kotlin.plugin)
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

gradlePlugin {
    plugins.register("mpp-configuration") {
        id = "mpp-configuration"
        implementationClass = "com.badoo.reaktive.configuration.ConfigurationPlugin"
    }
    plugins.register("publish") {
        id = "publish"
        implementationClass = "PublishPlugin"
    }
}
