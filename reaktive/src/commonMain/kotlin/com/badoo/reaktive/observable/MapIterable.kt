package com.badoo.reaktive.observable

fun <T, R> Observable<Iterable<T>>.mapIterable(mapper: (T) -> R): Observable<List<R>> = map { it.map(mapper) }

fun <T, R, C : MutableCollection<R>> Observable<Iterable<T>>.mapIterableTo(collectionSupplier: () -> C, mapper: (T) -> R): Observable<C> =
    map { it.mapTo(collectionSupplier(), mapper) }
