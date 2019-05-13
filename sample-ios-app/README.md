Kotlin/Native interops with Objective-C only for now, so Swift support is limited and generics are lost.

[Swift support](https://github.com/JetBrains/kotlin-native/pull/2850) is on it's way, will be released in some future release.

To use Reactive efficiently we advice you to write all Reactive specific code in Kotlin and compile it to library,
that will be used in Xcode project later.

Before opening Xcode with sample project build native library with command `./gradlew sample-ios-app:iosSimMainBinaries`.

See [Multiplatform Project: iOS and Android](https://kotlinlang.org/docs/tutorials/native/mpp-ios-android.html)
for further details how to setup build.