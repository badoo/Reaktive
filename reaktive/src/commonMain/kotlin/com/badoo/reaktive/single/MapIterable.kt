package com.badoo.reaktive.single

/**
 * Converts values of the [Iterable] emitted by the [Single] using the provided [mapper] and emits the resulting values as [List].
 */
fun <T, R> Single<Iterable<T>>.mapIterable(mapper: (T) -> R): Single<List<R>> = map { it.map(mapper) }

/**
 * Same as [mapIterable] but saves resulting values into a [MutableCollection] returned by [collectionSupplier].
 */
fun <T, R, C : MutableCollection<R>> Single<Iterable<T>>.mapIterableTo(collectionSupplier: () -> C, mapper: (T) -> R): Single<C> =
    map { it.mapTo(collectionSupplier(), mapper) }
