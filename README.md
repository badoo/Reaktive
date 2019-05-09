# <img src="https://raw.githubusercontent.com/badoo/Reaktive/master/assets/logo_reaktive.png" height="36">

[![](https://jitpack.io/v/badoo/Reaktive.svg)](https://jitpack.io/#badoo/Reaktive)
[![Build Status](https://travis-ci.org/badoo/Reaktive.svg?branch=master)](https://travis-ci.org/badoo/Reaktive)
[![](https://img.shields.io/badge/License-Apache/2.0-blue.svg)](https://github.com/badoo/Reaktive/blob/master/LICENSE)

Kotlin multi-platform implementation of Reactive Extensions.

Library status: under development, alpha pre-release is available, public API is subject to change

### Setup
Add JitPack repository into your root build.gradle file:
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the following dependencies into your module's build.gradle file:
#### Main library
Kotlin multi-platform:
```groovy
implementation 'com.github.badoo.reaktive:reaktive:<latest-version>'
```
Kotlin Android:
```groovy
implementation 'com.github.badoo.reaktive:reaktive-android:<latest-version>'
```
Kotlin JVM:
```groovy
implementation 'com.github.badoo.reaktive:reaktive-jvm:<latest-version>'
```
Kotlin JavaScript:
```groovy
implementation 'com.github.badoo.reaktive:reaktive-js:<latest-version>'
```
Kotlin Linux x64:
```groovy
implementation 'com.github.badoo.reaktive:reaktive-linuxx64:<latest-version>'
```
Kotlin Linux ARM 32 hfp:
```groovy
implementation 'com.github.badoo.reaktive:reaktive-linuxarm32hfp:<latest-version>'
```

#### RxJava2 interoperability
```groovy
implementation 'com.github.badoo.reaktive:rxjava2-interop:<latest-version>'
```

### Features:
* Multiplatform: JVM, Android, JavaScript, Linux X64, Linux ARM 32 hfp, iOS is under development
* Schedulers support: computation, IO, trampoline, main
* Supported sources: Observable, Maybe, Single, Completable
* Subjects: PublishSubject, BehaviorSubject
* Interoperability with RxJava2: convertion of sources between Reaktive and RxJava2, ability to reuse RxJava2's schedulers
* Supported operators:
  * Observable: asCompletable, collect, combineLatest, concatMap, debounce, distinctUntilChanged, doOnBeforeXxx, filter, firstOrComplete, firstOrDefault, firstOrError, flatMap, flatMapCompletable, flatMapMaybe, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, sample, scan, subscribeOn, throttle, toCompletable, toList, withLatestFrom, zip
  * Maybe: asCompletable, asObservable, asSingle, concat, doOnBeforeXxx, filter, flatMap, flatMapCompletable, flatMapObservable, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, subscribeOn, zip
  * Single: asCompletable, asMaybe, asObservable, blockingGet, concat, doOnBeforeXxx, flatMap, flatMapCompletable, flatMapMaybe, flatMapObservable, flatten, map, merge, notNull, observeOn, subscribeOn, zip
  * Completable: asMaybe, asObservable, asSingle, concat, doOnBeforeXxx, merge, observeOn, subscribeOn
  * Plus multiple factory and conversion functions
