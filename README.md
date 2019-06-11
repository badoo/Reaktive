# <img src="https://raw.githubusercontent.com/badoo/Reaktive/master/assets/logo_reaktive.png" height="36">

[![Download](https://api.bintray.com/packages/badoo/maven/reaktive/images/download.svg)](https://bintray.com/badoo/maven/reaktive/_latestVersion)
[![Build Status](https://travis-ci.org/badoo/Reaktive.svg?branch=master)](https://travis-ci.org/badoo/Reaktive)
[![License](https://img.shields.io/badge/License-Apache/2.0-blue.svg)](https://github.com/badoo/Reaktive/blob/master/LICENSE)

Kotlin multiplatform implementation of Reactive Extensions.

Library status: under development, beta pre-release is available, public API is subject to change

### Setup
Add Bintray repository into your root build.gradle file:
```groovy
repositories {
    maven {
        url  "https://dl.bintray.com/badoo/maven"
    }
}
```

There are four modules published:
- `reaktive` - the main Reaktive library (multiplatform)
- `reaktive-annotations` - collection of annotations (mutiplatform)
- `reaktive-test` - testing utilities (multiplatform)
- `rxjava2-interop` - RxJava2 interoperability helpers (JVM and Android)

Each multiplatform module is compiled against each target and published in
[metadata publishing mode](https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#experimental-metadata-publishing-mode). 

#### Multiplatform modules

Kotlin common:
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
implementation 'com.badoo.reaktive:<module-name>-sim:<latest-version>'
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

### Features:
* Multiplatform: JVM, Android, iOS, JavaScript, Linux X64, Linux ARM 32 hfp
* Schedulers support: computation, IO, trampoline, main
* True multithreading for Kotlin/Native (there are some [limitations](https://kotlinlang.org/docs/reference/native/concurrency.html#object-transfer-and-freezing))
* Supported sources: Observable, Maybe, Single, Completable
* Subjects: PublishSubject, BehaviorSubject
* Interoperability with RxJava2: conversion of sources between Reaktive and RxJava2, ability to reuse RxJava2's schedulers
* Supported operators:
  * Observable: asCompletable, collect, combineLatest, concatMap, debounce, defaultIfEmpty, distinctUntilChanged, doOnBeforeXxx, filter, firstOrComplete, firstOrDefault, firstOrError, flatMap, flatMapCompletable, flatMapMaybe, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, sample, scan, skip, subscribeOn, switchIfEmpty, throttle, toCompletable, toList, withLatestFrom, zip
  * Maybe: asCompletable, asObservable, asSingle, concat, doOnBeforeXxx, filter, flatMap, flatMapCompletable, flatMapObservable, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, subscribeOn, zip
  * Single: asCompletable, asMaybe, asObservable, blockingGet, concat, doOnBeforeXxx, filter, flatMap, flatMapCompletable, flatMapMaybe, flatMapObservable, flatten, map, merge, notNull, observeOn, subscribeOn, zip
  * Completable: andThen, asMaybe, asObservable, asSingle, concat, doOnBeforeXxx, merge, observeOn, subscribeOn
  * Plus multiple factory and conversion functions

### Samples:
* [Android app](https://github.com/badoo/Reaktive/tree/master/sample-android-app)
* [iOS app](https://github.com/badoo/Reaktive/tree/master/sample-ios-app)
* [JavaScript browser app](https://github.com/badoo/Reaktive/tree/master/sample-js-browser-app)
* [Linux x64 app](https://github.com/badoo/Reaktive/tree/master/sample-linuxx64-app)
