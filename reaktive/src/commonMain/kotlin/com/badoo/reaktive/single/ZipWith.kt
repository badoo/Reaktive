package com.badoo.reaktive.single

/**
 * Subscribes to both the current [Single] and the [other] [Single], accumulates their values and emits a value returned by the [mapper] function.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#zipWith-io.reactivex.SingleSource-io.reactivex.functions.BiFunction-).
 */
fun <T, R, I> Single<T>.zipWith(other: Single<R>, mapper: (T, R) -> I): Single<I> =
    zip(this, other) { first, second -> mapper(first, second) }
