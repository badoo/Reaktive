package com.badoo.reaktive.maybe

fun <T, R> Maybe<T>.zipWith(maybe: Maybe<R>): Maybe<Pair<T, R>> =
    zip(this, maybe) { first, second -> first to second }

fun <T, R, I> Maybe<Pair<T, R>>.zipWith(maybe: Maybe<I>): Maybe<Triple<T, R, I>> =
    zip(this, maybe) { pair, other -> Triple(pair.first, pair.second, other) }
