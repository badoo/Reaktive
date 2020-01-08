# <img src="https://raw.githubusercontent.com/badoo/Reaktive/master/assets/logo_reaktive.png" height="36">

[![Download](https://api.bintray.com/packages/badoo/maven/reaktive/images/download.svg)](https://bintray.com/badoo/maven/reaktive/_latestVersion)
[![Build Status](https://github.com/badoo/Reaktive/workflows/Build/badge.svg?branch=master)](https://github.com/badoo/Reaktive/actions)
[![License](https://img.shields.io/badge/License-Apache/2.0-blue.svg)](https://github.com/badoo/Reaktive/blob/master/LICENSE)

Kotlin multiplatform implementation of Reactive Extensions.

### Setup
Recommended minimum Gradle version is 5.3. Please read first the documentation about
[metadata publishing mode](https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#experimental-metadata-publishing-mode).

Add Bintray repository into your root build.gradle file:
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/badoo/maven"
    }
}
```

There are a number of modules published:
- `reaktive` - the main Reaktive library (multiplatform)
- `reaktive-annotations` - collection of annotations (mutiplatform)
- `reaktive-testing` - testing utilities (multiplatform)
- `utils` - some utilities like `Clock`, `AtomicReference`, `Lock`, etc. (multiplatform)
- `coroutines-interop` - Kotlin coroutines interoperability helpers (multiplatform)
- `rxjava2-interop` - RxJava2 interoperability helpers (JVM and Android)
- `rxjava3-interop` - RxJava3 interoperability helpers (JVM and Android)

#### Multiplatform module publications

Kotlin common (root publication):
```groovy
implementation 'com.badoo.reaktive:<module-name>:<latest-version>'
```
JVM:
```groovy
implementation 'com.badoo.reaktive:<module-name>-jvm:<latest-version>'
```
Android (debug and release):
```groovy
implementation 'com.badoo.reaktive:<module-name>-android:<latest-version>'
```
iOS 32:
```groovy
implementation 'com.badoo.reaktive:<module-name>-ios32:<latest-version>'
```
iOS 64:
```groovy
implementation 'com.badoo.reaktive:<module-name>-ios64:<latest-version>'
```
iOS sim:
```groovy
implementation 'com.badoo.reaktive:<module-name>-iossim:<latest-version>'
```
macOS x64:
```groovy
implementation 'com.badoo.reaktive:<module-name>-macosx64:<latest-version>'
```
watchOS ARM32
```groovy
implementation 'com.badoo.reaktive:<module-name>-watchosarm32:<latest-version>'
```
watchOS ARM64
```groovy
implementation 'com.badoo.reaktive:<module-name>-watchosarm64:<latest-version>'
```
watchOS sim
```groovy
implementation 'com.badoo.reaktive:<module-name>-watchossim:<latest-version>'
```
tvOS ARM64
```groovy
implementation 'com.badoo.reaktive:<module-name>-tvosarm64:<latest-version>'
```
tvOS sim
```groovy
implementation 'com.badoo.reaktive:<module-name>-tvossim:<latest-version>'
```
JavaScript:
```groovy
implementation 'com.badoo.reaktive:<module-name>-js:<latest-version>'
```
Linux x64:
```groovy
implementation 'com.badoo.reaktive:<module-name>-linuxx64:<latest-version>'
```
Linux ARM 32 hfp:
```groovy
implementation 'com.badoo.reaktive:<module-name>-linuxarm32hfp:<latest-version>'
```

#### Regular modules:
```groovy
implementation 'com.badoo.reaktive:<module-name>:<latest-version>'
```

#### Typical dependencies configuration for MPP module (metadata mode)
```groovy
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation 'com.badoo.reaktive:reaktive:<latest-version>'
                implementation 'com.badoo.reaktive:reaktive-annotations:<latest-version>'
                implementation 'com.badoo.reaktive:coroutines-interop:<latest-version>'
            }
        }

        commonTest {
            dependencies {
                implementation 'com.badoo.reaktive:reaktive-testing:<latest-version>'
            }
        }
    }
}
```

### Features:
* Multiplatform: JVM, Android, iOS, macOS, watchOS, tvOS, JavaScript, Linux X64, Linux ARM 32 hfp
* Schedulers support: computation, IO, trampoline, main
* True multithreading for Kotlin/Native (there are some [limitations](https://kotlinlang.org/docs/reference/native/concurrency.html#object-transfer-and-freezing))
* Thread local subscriptions without freezing for Kotlin/Native
* Supported sources: Observable, Maybe, Single, Completable
* Subjects: PublishSubject, BehaviorSubject, ReplaySubject, UnicastSubject
* Interoperability with Kotlin Coroutines: conversions between coroutines (including Flow) and Reaktive
* Interoperability with RxJava2 and RxJava3: conversion of sources between Reaktive and RxJava, ability to reuse RxJava's schedulers

### Kotlin Native pitfalls
Kotlin Native memory model and concurrency are very special. In general shared mutable state between threads is not allowed.
Since Reaktive supports multithreading in Kotlin Native, please read the following documents before using it:
* [Concurrency](https://kotlinlang.org/docs/reference/native/concurrency.html#object-transfer-and-freezing)
* [Immutability](https://kotlinlang.org/docs/reference/native/immutability.html)

Object detachment is relatively difficult to achieve and is very error-prone when the objects are created from outside and
are not fully managed by the library. This is why Reaktive prefers frozen state. Here are some hints:
* Any callback (and any captured objects) submitted to a Scheduler will be frozen
* `subscribeOn` freezes both its upstream source and downstream observer,
all the Disposables (upstream's and downstream's) are frozen as well,
all the values (including errors) are **not** frozen by the operator
* `observeOn` freezes only its downstream observer and all the values (including errors) passed through it, plus all the Disposables,
upstream source is **not** frozen by the operator
* Other operators that use scheduler (like `debounce`, `timer`, `delay`, etc.) behave same as `observeOn` in most of the cases

#### Thread local tricks to avoid freezing
Sometimes freezing is not acceptable, e.g. we might want to load some data in background and then update the UI.
Obviously UI can not be frozen. With Reaktive it is possible to achieve such a behaviour in two ways:

Use `threadLocal` operator:
```kotlin
val values = mutableListOf<Any>()
var isFinished = false

observable<Any> { emitter ->
    // Background job
}
    .subscribeOn(ioScheduler)
    .observeOn(mainScheduler)
    .threadLocal()
    .doOnBeforeNext { values += it } // Callback is not frozen, we can updated the mutable list
    .doOnBeforeFinally { isFinished = true } // Callback is not frozen, we can change the flag
    .subscribe()
```

Set `isThreadLocal` flag to `true` in `subscribe` operator:
```kotlin
val values = mutableListOf<Any>()
var isComplete = false

observable<Any> { emitter ->
    // Background job
}
    .subscribeOn(ioScheduler)
    .observeOn(mainScheduler)
    .subscribe(
        isThreadLocal = true,
        onNext = { values += it }, // Callback is not frozen, we can updated the mutable list
        onComplete = { isComplete = true } // Callback is not frozen, we can change the flag
    )
```

In both cases subscription (`subscribe` call) **must** be performed on the Main thread.

### Subscription management with DisposableScope

Reaktive provides an easy way to manage subscriptions: [DisposableScope](https://github.com/badoo/Reaktive/blob/master/reaktive/src/commonMain/kotlin/com/badoo/reaktive/disposable/scope/DisposableScope.kt).

Take a look at the following examples:

```kotlin
val scope =
    disposableScope {
        observable.subscribeScoped(...) // Subscription will be disposed when the scope is disposed

        doOnDispose {
            // Will be called when the scope is disposed
        }

        someDisposable.scope() // `someDisposable` will be disposed when the scope is disposed
    }

// At some point later
scope.dispose()
```

```kotlin
class MyPresenter(
    private val view: MyView,
    private val longRunningAction: Completable
) : DisposableScope by DisposableScope() {

    init {
        doOnDispose {
            // Will be called when the presenter is disposed
        }
    }

    fun load() {
        view.showProgressBar()

        // Subscription will be disposed when the presenter is disposed
        longRunningAction.subscribeScoped(onComplete = view::hideProgressBar)
    }
}

class MyActivity : AppCompatActivity(), DisposableScope by DisposableScope() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MyPresenter(...).scope()
    }

    override fun onDestroy() {
        dispose()

        super.onDestroy()
    }
}
```

### Samples:
* [MPP module](https://github.com/badoo/Reaktive/tree/master/sample-mpp-module)
* [Android app](https://github.com/badoo/Reaktive/tree/master/sample-android-app)
* [iOS app](https://github.com/badoo/Reaktive/tree/master/sample-ios-app)
* [macOS app](https://github.com/badoo/Reaktive/tree/master/sample-macos-app)
* [JavaScript browser app](https://github.com/badoo/Reaktive/tree/master/sample-js-browser-app)
* [Linux x64 app](https://github.com/badoo/Reaktive/tree/master/sample-linuxx64-app)
