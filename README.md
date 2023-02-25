# <img src="https://raw.githubusercontent.com/badoo/Reaktive/master/assets/logo_reaktive.png" height="36">

[![Maven Central](https://img.shields.io/maven-central/v/com.badoo.reaktive/reaktive?color=blue)](https://search.maven.org/artifact/com.badoo.reaktive/reaktive)
[![Build Status](https://github.com/badoo/Reaktive/workflows/Build/badge.svg?branch=master)](https://github.com/badoo/Reaktive/actions)
[![License](https://img.shields.io/badge/License-Apache/2.0-blue.svg)](https://github.com/badoo/Reaktive/blob/master/LICENSE)
[![kotlinlang|reaktive](https://img.shields.io/badge/kotlinlang-reaktive-blue?logo=slack)](https://kotlinlang.slack.com/archives/CU05HB31A)

Kotlin multiplatform implementation of Reactive Extensions.

Should you have any questions or feedback welcome to the **Kotlin Slack channel**: 
[#reaktive](https://kotlinlang.slack.com/archives/CU05HB31A)

### Setup

There are a number of modules published to Maven Central:

- `reaktive` - the main Reaktive library (multiplatform)
- `reaktive-annotations` - collection of annotations (mutiplatform)
- `reaktive-testing` - testing utilities (multiplatform)
- `utils` - some utilities like `Clock`, `AtomicReference`, `Lock`, etc. (multiplatform)
- `coroutines-interop` - Kotlin coroutines interoperability helpers (multiplatform)
- `rxjava2-interop` - RxJava v2 interoperability helpers (JVM and Android)
- `rxjava3-interop` - RxJava v3 interoperability helpers (JVM and Android)

#### Configuring dependencies

```groovy
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation 'com.badoo.reaktive:reaktive:<version>'
                implementation 'com.badoo.reaktive:reaktive-annotations:<version>'
                implementation 'com.badoo.reaktive:coroutines-interop:<version>' // For interop with coroutines
                implementation 'com.badoo.reaktive:rxjava2-interop:<version>' // For interop with RxJava v2
                implementation 'com.badoo.reaktive:rxjava3-interop:<version>' // For interop with RxJava v3
            }
        }

        commonTest {
            dependencies {
                implementation 'com.badoo.reaktive:reaktive-testing:<version>'
            }
        }
    }
}
```

### Features:

* Multiplatform: JVM, Android, iOS, macOS, watchOS, tvOS, JavaScript, Linux X64, Linux ARM 32 hfp
* Schedulers support: 
  * `computationScheduler` - fixed thread pool equal to a number of cores
  * `ioScheduler` - unbound thread pool with caching policy
  * `newThreadScheduler` - creates a new thread for each unit of work
  * `singleScheduler` - executes tasks on a single shared background thread
  * `trampolineScheduler` - queues tasks and executes them on one of the participating threads
  * `mainScheduler` - executes tasks on main thread
* True multithreading for Kotlin/Native (there are some [limitations](https://kotlinlang.org/docs/reference/native/concurrency.html#object-transfer-and-freezing))
* Thread local subscriptions without freezing for Kotlin/Native
* Supported sources: `Observable`, `Maybe`, `Single`, `Completable`
* Subjects: `PublishSubject`, `BehaviorSubject`, `ReplaySubject`, `UnicastSubject`
* Interoperability with Kotlin Coroutines: conversions between coroutines (including Flow) and Reaktive
* Interoperability with RxJava2 and RxJava3: conversion of sources between Reaktive and RxJava, ability to reuse RxJava's schedulers

### Reaktive and the old (strict) Kotlin/Native memory model

The old (strict) Kotlin Native memory model and concurrency are very special. In general shared mutable state between threads is not allowed.
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
    .doOnBeforeNext { values += it } // Callback is not frozen, we can update the mutable list
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
        onNext = { values += it }, // Callback is not frozen, we can update the mutable list
        onComplete = { isComplete = true } // Callback is not frozen, we can change the flag
    )
```

In both cases subscription (`subscribe` call) **must** be performed on the Main thread.

### Reaktive and the new (relaxed) Kotlin/Native memory model

The new (relaxed) Kotlin/Native [memory model](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md)
allows passing objects between threads without freezing. When using this memory model, there is no need
to use the `threadLocal` operator/argument anymore. Please make sure that you also **disabled freezing**
as [described in the documentation](https://github.com/JetBrains/kotlin/blob/master/kotlin-native/NEW_MM.md#unexpected-object-freezing).

### Coroutines interop

This functionality is provided by the `coroutines-interop` module which is published in two versions:

- `coroutines-interop:<version>` is based on stable `kotlinx.coroutines` - use this variant with the stable version of coroutines **and** with the old (strict) memory model.
- `coroutines-interop:<version>-nmtc` is based on [work-in-progress](https://github.com/Kotlin/kotlinx.coroutines/pull/1648) multi-threaded `kotlinx.coroutines` - use this variant with either the multi-threaded version of coroutines **or** the new (relaxed) memory model.

#### Coroutines interop based on stable kotlinx.coroutines

There are few important limitations:

- Neither `Job` nor `CoroutineContext` can be frozen (until release of the multi-threaded coroutines).
- Because of the first limitation all `xxxFromCoroutine {}` builders and `Flow.asObservable()` converter are executed inside `runBlocking` block in Kotlin/Native and should be subscribed on a background `Scheduler`.

Consider the following example for `coroutines-interop`:

```kotlin
singleFromCoroutine {
    // This block will be executed inside `runBlocking` in Kotlin/Native
}
    .subscribeOn(ioScheduler) // Switching to a background thread is necessary
    .observeOn(mainScheduler)
    .subscribe { /* Get the result here */ }
```

Please note that Ktor uses multi-threaded coroutines by default. If you are using Ktor, please use `coroutines-interop` module based on multi-threaded coroutines and proceed to the next Readme section.


#### Coroutines interop based on multi-threaded kotlinx.coroutines

The multi-threaded `kotlinx.coroutines` variant lifts some unpleasant restrictions - both `Job` and `CoroutineContext` can be frozen.

So there is one crucial difference - all `xxxFromCoroutine {}` builders and `Flow.asObservable()` converter are executed asynchronously in all targets (including Kotlin/Native), so can be subscribed on any scheduler.

Notes:

- Because multi-threaded coroutines are work-in-progress, there are possible [issues](https://github.com/Kotlin/kotlinx.coroutines/blob/native-mt/kotlin-native-sharing.md#known-problems).
- Ktor can be used out of the box without any known limitations

##### Coroutines interop general limitations

Converters `Scheduler.asCoroutineDispatcher()` and `CoroutineContext.asScheduler()` are available only in JVM and JS currently.

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

### Reaktive and Swift interoperability

Please see the corresponding documentation page: [Reaktive and Swift interoperability](docs/SwiftInterop.md).

### Plugins

Reaktive provides Plugin API, something similar to [RxJava plugins](https://github.com/ReactiveX/RxJava/wiki/Plugins). The Plugin API provides a way to decorate Reaktive sources. A plugin should implement the [ReaktivePlugin](https://github.com/badoo/Reaktive/blob/master/reaktive/src/commonMain/kotlin/com/badoo/reaktive/plugin/ReaktivePlugin.kt) interface, and can be registered using the `registerReaktivePlugin` function and unregistered using the `unregisterReaktivePlugin` function.

```kotlin
object MyPlugin : ReaktivePlugin {
    override fun <T> onAssembleObservable(observable: Observable<T>): Observable<T> =
        object : Observable<T> {
            private val traceException = TraceException()

            override fun subscribe(observer: ObservableObserver<T>) {
                observable.subscribe(
                    object : ObservableObserver<T> by observer {
                        override fun onError(error: Throwable) {
                            observer.onError(error, traceException)
                        }
                    }
                )
            }
        }

    override fun <T> onAssembleSingle(single: Single<T>): Single<T> =
        TODO("Similar to onAssembleSingle")

    override fun <T> onAssembleMaybe(maybe: Maybe<T>): Maybe<T> = 
        TODO("Similar to onAssembleSingle")

    override fun onAssembleCompletable(completable: Completable): Completable =
        TODO("Similar to onAssembleSingle")

    private fun ErrorCallback.onError(error: Throwable, traceException: TraceException) {
        if (error.suppressedExceptions.lastOrNull() !is TraceException) {
            error.addSuppressed(traceException)
        }
        onError(error)
    }

    private class TraceException : Exception()
}
```

### Samples:
* [MPP module](https://github.com/badoo/Reaktive/tree/master/sample-mpp-module)
* [Android app](https://github.com/badoo/Reaktive/tree/master/sample-android-app)
* [iOS app](https://github.com/badoo/Reaktive/tree/master/sample-ios-app)
* [JavaScript browser app](https://github.com/badoo/Reaktive/tree/master/sample-js-browser-app)
* [Linux x64 app](https://github.com/badoo/Reaktive/tree/master/sample-linuxx64-app)
