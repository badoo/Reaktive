plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.kotlin.plugin)
    implementation(libs.android.plugin)
    implementation(libs.jmh.plugin)
    implementation(libs.detekt.plugin)
    implementation(libs.shadow)
    implementation(libs.kotlinx.compatibility)
    implementation(libs.publish.plugin)
    implementation(libs.dokka.plugin)
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
