# Reaktive
Kotlin multiplatform implementation of Reactive Extensions

Library status: under development, preparing for alpha release

Features:
* Multiplatform: JVM and Android, iOS is under development
* Schedulers support: computation, IO, trampoline, main
* Supported sources: Observable, Single, Maybe, Completable
* Supported operators:
  * Observable: asCompletable, collect, combineLatest, concatMap, debounce, doOnBeforeXxx, filter, firstOrComplete, firstOrDefault, firstOrError, flatMap, flatMapCompletable, flatMapMaybe, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, sample, subscribeOn, throttle, toCompletable, toList, zip
  * Maybe: asCompletable, asObservable, asSingle, concat, doOnBeforeXxx, filter, flatMap, flatMapCompletable, flatMapObservable, flatMapSingle, flatten, map, merge, notNull, observeOn, ofType, subscribeOn, zip
  * Single: asCompletable, asMaybe, asObservable, concat, doOnBeforeXxx, flatMap, flatMapCompletable, flatMapMaybe, flatMapObservable, flatten, map, merge, notNull, observeOn, subscribeOn, zip
  * Completable: asMaybe, asObservable, asSingle, concat, doOnBeforeXxx, merge, observeOn, subscribeOn
  * Plus multiple factory and conversion functions
