package com.badoo.reaktive.single

/**
 * When the [Single] signals `onError`, emits a value returned by [valueSupplier].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#onErrorReturn-io.reactivex.functions.Function-).
 */
fun <T> Single<T>.onErrorReturn(valueSupplier: (Throwable) -> T): Single<T> =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toSingle() }

/**
 * When the [Single] signals `onError`, emits the [value].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#onErrorReturnItem-T-).
 */
fun <T> Single<T>.onErrorReturnValue(value: T): Single<T> =
    onErrorResumeNext { value.toSingle() }
