package com.badoo.reaktive.observable

/**
 * Maps each [Iterable] emitted by the [Observable] using the provided [mapper]
 * and emits [List]s with the result elements. See [map] for more information.
 */
fun <T, R> Observable<Iterable<T>>.mapIterable(mapper: (T) -> R): Observable<List<R>> = map { it.map(mapper) }

/**
 * Maps each [Iterable] emitted by the [Observable] using the provided [mapper]
 * into a [MutableCollection] returned by [collectionSupplier]. Emits [MutableCollection]s
 * with the result elements. See [map] for more information.
 */
fun <T, R, C : MutableCollection<R>> Observable<Iterable<T>>.mapIterableTo(collectionSupplier: () -> C, mapper: (T) -> R): Observable<C> =
    map { it.mapTo(collectionSupplier(), mapper) }
