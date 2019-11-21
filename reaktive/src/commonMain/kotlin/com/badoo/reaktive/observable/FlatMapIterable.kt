package com.badoo.reaktive.observable

fun <T, R> Observable<T>.flatMapIterable(transformer: (T) -> Iterable<R>): Observable<R> =
    flatMap { transformer(it).asObservable() }
