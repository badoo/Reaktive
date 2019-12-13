package com.badoo.reaktive.single

fun <T, R> Single<T>.zipWith(single: Single<R>): Single<Pair<T, R>> =
    zip(this, single) { first, second -> first to second }

fun <T, R, I> Single<Pair<T, R>>.zipWith(single: Single<I>): Single<Triple<T, R, I>> =
    zip(this, single) { pair, other -> Triple(pair.first, pair.second, other) }
