package com.badoo.reaktive.observable

fun <T, R> Observable<T>.zipWith(other: Observable<R>): Observable<Pair<T, R>> =
    zip(this, other) { first, second -> first to second }

fun <T, R, I> Observable<T>.zipWith(other: Observable<R>, zipper: (T, R) -> I): Observable<I> =
    zip(this, other) { first, second -> zipper(first, second) }
