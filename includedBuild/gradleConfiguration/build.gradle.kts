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
    implementation(Deps.kotlin.compilerEmbeddable)
    implementation(Deps.android.plugin)
    implementation(Deps.jmh.plugin)
    implementation(Deps.detekt.plugin)
    implementation(Deps.shadow)
    implementation(Deps.kotlinx.compatibility)
    implementation("com.badoo.reaktive.dependencies:dependencies:SNAPSHOT")
}

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
