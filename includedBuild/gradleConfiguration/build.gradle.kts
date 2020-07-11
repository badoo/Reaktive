import com.badoo.reaktive.dependencies.Deps

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    id("dependencies")
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
    implementation(Deps.kotlinx.compatibility)
    // Does not work in IDEA
    // implementation("com.badoo.reaktive.dependencies:dependencies:SNAPSHOT")
}

// Same as 'implementation("com.badoo.reaktive.dependencies:dependencies:SNAPSHOT")', but will make autocompletion work
kotlin.sourceSets.getByName("main").kotlin.srcDir("../dependencies/src/main/kotlin")

gradlePlugin {
    plugins.register("mpp-configuration") {
        id = "mpp-configuration"
        implementationClass = "com.badoo.reaktive.configuration.MppConfigurationPlugin"
    }
    plugins.register("publish-configuration") {
        id = "publish-configuration"
        implementationClass = "com.badoo.reaktive.publish.PublishConfigurationPlugin"
    }
    plugins.register("binary-compatibility-configuration") {
        id = "binary-compatibility-configuration"
        implementationClass = "com.badoo.reaktive.compatibility.BinaryCompatibilityConfigurationPlugin"
    }
    plugins.register("detekt-configuration") {
        id = "detekt-configuration"
        implementationClass = "com.badoo.reaktive.detekt.DetektConfigurationPlugin"
    }
}
