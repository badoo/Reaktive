enableFeaturePreview("GRADLE_METADATA")

logger.warn("Current environment: ${Target.currentTarget(this)}")

include(":reaktive")
include(":reaktive-testing")
include(":reaktive-annotations")
include(":utils")
include(":coroutines-interop")
include(":sample-mpp-module")
if (Target.shouldDefineTarget(this, Target.JVM)) {
    include(":rxjava2-interop")
    include(":rxjava3-interop")
    include(":sample-android-app")
    include(":benchmarks:jmh")
    include(":tools:binary-compatibility")
}
if (Target.shouldDefineTarget(this, Target.JS)) {
    include(":sample-js-browser-app")
}
if (Target.shouldDefineTarget(this, Target.LINUX)) {
    include(":sample-linuxx64-app")
}
if (Target.shouldDefineTarget(this, Target.IOS)) {
    include(":sample-ios-app")
}
if (Target.shouldDefineTarget(this, Target.MACOS)) {
    include(":sample-macos-app")
}
