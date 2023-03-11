pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

includeBuild("includedBuild/gradleConfiguration")

if (!startParameter.projectProperties.containsKey("check_publication")) {
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
} else {
    include(":tools:check-publication")
}
