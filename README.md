## Reaktive
Kotlin multiplatform implementation of Reactive Extensions

Library status: under development, pre-release is available
Latest version: [![](https://jitpack.io/v/badoo/Reaktive.svg)](https://jitpack.io/#badoo/Reaktive)

### Setup
Add JitPack repository into your root build.gradle file:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the following dependencies into your module's build.gradle file:
##### Main library
```
implementation 'com.github.badoo.reaktive:reaktive:<latest-version>'
```

##### RxJava2 interoperability
```
implementation 'com.github.badoo.reaktive:rxjava2-interop:<latest-version>'
```

### Features:
* Multiplatform: JVM and Android, iOS is under development
* Schedulers support: computation, IO, trampoline, main
* Supported sources: Observable, Single, Maybe, Completable
* Interoperability with RxJava2: convertion of sources between Reaktive and RxJava2, ability to reuse RxJava2's schedulers
* Supported operators:
  * Observable: asCompletable, collect, combineLatest, concatMap, debounce, doOnBeforeXxx, filter, firstOrComplete, firstOrDefault, firstOrError, flatMap, flatMapCompletable, flatMapMaybe, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, sample, subscribeOn, throttle, toCompletable, toList, zip
  * Maybe: asCompletable, asObservable, asSingle, concat, doOnBeforeXxx, filter, flatMap, flatMapCompletable, flatMapObservable, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, subscribeOn, zip
  * Single: asCompletable, asMaybe, asObservable, concat, doOnBeforeXxx, flatMap, flatMapCompletable, flatMapMaybe, flatMapObservable, flatten, map, merge, notNull, observeOn, subscribeOn, zip
  * Completable: asMaybe, asObservable, asSingle, concat, doOnBeforeXxx, merge, observeOn, subscribeOn
  * Plus multiple factory and conversion functions
