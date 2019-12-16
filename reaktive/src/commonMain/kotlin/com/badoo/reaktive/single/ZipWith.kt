package com.badoo.reaktive.single

fun <T, R> Single<T>.zipWith(other: Single<R>): Single<Pair<T, R>> =
    zip(this, other) { first, second -> first to second }

fun <T, R, I> Single<T>.zipWith(other: Single<R>, mapper: (T, R) -> I): Single<I> =
    zip(this, other) { first, second -> mapper(first, second) }
