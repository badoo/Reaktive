package com.badoo.reaktive.maybe

fun <T, R> Maybe<T>.zipWith(maybe: Maybe<R>): Maybe<Pair<T, R>> =
    zip(this, maybe) { first, second -> first to second }
