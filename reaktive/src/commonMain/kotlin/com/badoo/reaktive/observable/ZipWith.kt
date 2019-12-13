package com.badoo.reaktive.observable

fun <T, R> Observable<T>.zipWith(observable: Observable<R>): Observable<Pair<T, R>> =
    zip(this, observable) { first, second -> first to second }

fun <T, R, I> Observable<Pair<T, R>>.zipWith(observable: Observable<I>): Observable<Triple<T, R, I>> =
    zip(this, observable) { pair, other -> Triple(pair.first, pair.second, other) }
