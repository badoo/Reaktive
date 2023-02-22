pluginManagement {
    repositories {
        google()
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }
}

includeBuild("includedBuild/dependencies")
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
} else {
    include(":tools:check-publication")
}
