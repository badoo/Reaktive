Kotlin/Native interops with Objective-C only for now, so Swift support is limited and generics are lost.

[Swift support](https://github.com/JetBrains/kotlin-native/pull/2850) is on it's way, will be released in some future release.

Before opening Xcode with sample project build native library with command `./gradlew sample-ios-app:iosSimMainBinaries`.
Also install all required Pod dependencies via `pod install`.

See [Multiplatform Project: iOS and Android](https://kotlinlang.org/docs/tutorials/native/mpp-ios-android.html)
for further details how to setup build.