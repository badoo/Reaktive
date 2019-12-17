package com.badoo.reaktive.observable

fun <T, R, I> Observable<T>.zipWith(other: Observable<R>, mapper: (T, R) -> I): Observable<I> =
    zip(this, other) { first, second -> mapper(first, second) }
