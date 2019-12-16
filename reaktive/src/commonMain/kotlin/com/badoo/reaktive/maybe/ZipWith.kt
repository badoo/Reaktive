package com.badoo.reaktive.maybe

fun <T, R> Maybe<T>.zipWith(other: Maybe<R>): Maybe<Pair<T, R>> =
    zip(this, other) { first, second -> first to second }

fun <T, R, I> Maybe<T>.zipWith(other: Maybe<R>, mapper: (T, R) -> I): Maybe<I> =
    zip(this, other) { first, second -> mapper(first, second) }
