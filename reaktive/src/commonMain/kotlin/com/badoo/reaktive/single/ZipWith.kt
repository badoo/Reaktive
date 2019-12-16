package com.badoo.reaktive.single

fun <T, R> Single<T>.zipWith(single: Single<R>): Single<Pair<T, R>> =
    zip(this, single) { first, second -> first to second }

fun <T, R, I> Single<T>.zipWith(single: Single<R>, mapper: (T, R) -> I): Single<I> =
    zip(this, single) { first, second -> mapper(first, second) }
