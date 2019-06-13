plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}

dependencies {
    implementation("com.moowork.gradle:gradle-node-plugin:1.3.1")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.31")
    implementation("com.android.tools.build:gradle:3.4.1")
    implementation("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
}