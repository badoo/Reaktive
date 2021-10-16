package com.badoo.reaktive.maybe

/**
 * When the [Maybe] signals `onError`, emits a value returned by [valueSupplier].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#onErrorReturn-io.reactivex.functions.Function-).
 */
fun <T> Maybe<T>.onErrorReturn(valueSupplier: (Throwable) -> T): Maybe<T> =
    onErrorResumeNext { throwable -> valueSupplier(throwable).toMaybe() }

/**
 * When the [Maybe] signals `onError`, emits the [value].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#onErrorReturnItem-T-).
 */
fun <T> Maybe<T>.onErrorReturnValue(value: T): Maybe<T> =
    onErrorResumeNext { value.toMaybe() }
