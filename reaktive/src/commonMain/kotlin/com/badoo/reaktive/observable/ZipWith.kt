package com.badoo.reaktive.observable

fun <T, R> Observable<T>.zipWith(observable: Observable<R>): Observable<Pair<T, R>> =
    zip(this, observable) { first, second -> first to second }

fun <T, R, I> Observable<T>.zipWith(observable: Observable<R>, mapper: (T, R) -> I): Observable<I> =
    zip(this, observable) { first, second -> mapper(first, second) }
