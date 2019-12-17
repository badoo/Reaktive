package com.badoo.reaktive.single

fun <T, R, I> Single<T>.zipWith(other: Single<R>, mapper: (T, R) -> I): Single<I> =
    zip(this, other) { first, second -> mapper(first, second) }
