plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

apply(from = "../gradle/versions.gradle.kts")

dependencies {
    implementation("com.moowork.gradle:gradle-node-plugin:${property("node_gradle_version")}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin_version")}")
    implementation("com.android.tools.build:gradle:${property("android_gradle_version")}")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:${property("bintray_gradle_version")}")
}