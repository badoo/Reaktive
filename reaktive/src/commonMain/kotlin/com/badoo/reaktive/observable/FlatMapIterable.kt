package com.badoo.reaktive.observable

/**
 * Calls the [transformer] for each element emitted by the [Observable] and emits all elements from the returned [Iterable]s.
 */
fun <T, R> Observable<T>.flatMapIterable(transformer: (T) -> Iterable<R>): Observable<R> =
    flatMap { transformer(it).asObservable() }
