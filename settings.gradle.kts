pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }
}

// Set up a different name for the root project because of https://youtrack.jetbrains.com/issue/KT-48407
rootProject.name = "Reaktive-root"

includeBuild("includedBuild/dependencies")
includeBuild("includedBuild/gradleConfiguration")

include(":utils")
include(":reaktive")
include(":reaktive-testing")
include(":reaktive-annotations")
include(":coroutines-interop")
include(":rxjava2-interop")
include(":rxjava3-interop")
include(":benchmarks:jmh")
include(":sample-mpp-module")
include(":sample-android-app")
include(":sample-js-browser-app")
include(":sample-linuxx64-app")
include(":sample-ios-app")
include(":sample-macos-app")

if (startParameter.projectProperties.containsKey("check_publication")) {
    include(":tools:check-publication")
}
