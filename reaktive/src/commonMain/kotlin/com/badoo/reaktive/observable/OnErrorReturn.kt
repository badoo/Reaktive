package com.badoo.reaktive.observable

/**
 * When the [Observable] signals `onError`, emits a value returned by [valueSupplier].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#onErrorReturn-io.reactivex.functions.Function-).
 */
fun <T> Observable<T>.onErrorReturn(valueSupplier: (Throwable) -> T): Observable<T> =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toObservable() }

/**
 * When the [Observable] signals `onError`, emits the [value].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#onErrorReturnItem-T-).
 */
fun <T> Observable<T>.onErrorReturnValue(value: T): Observable<T> =
    onErrorResumeNext { value.toObservable() }
