package com.badoo.reaktive.maybe

fun <T, R> Maybe<T>.zipWith(maybe: Maybe<R>): Maybe<Pair<T, R>> =
    zip(this, maybe) { first, second -> first to second }

fun <T, R, I> Maybe<T>.zipWith(maybe: Maybe<R>, mapper: (T, R) -> I): Maybe<I> =
    zip(this, maybe) { first, second -> mapper(first, second) }
