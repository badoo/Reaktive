import org.gradle.api.internal.FeaturePreviews.Feature

enableFeaturePreview(Feature.GRADLE_METADATA.name)

logger.warn("Current environment: ${Target.currentTarget(this)}")

include(":reaktive")
include(":reaktive-testing")
include(":reaktive-annotations")
include(":utils")
include(":coroutines-interop")
include(":sample-mpp-module")
include(":benchmarks:jmh")
include(":tools:binary-compatibility")
if (Target.shouldDefineTarget(this, Target.ALL_LINUX_HOSTED)) {
    include(":sample-android-app")
    include(":sample-js-browser-app")
    include(":sample-linuxx64-app")
    include(":rxjava2-interop")
    include(":rxjava3-interop")
}
