package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.mapIterable

/**
 * Converts values of the [Iterable] emitted by the [Maybe] using the provided [mapper] and emits the resulting values as [List].
 */
fun <T, R> Maybe<Iterable<T>>.mapIterable(mapper: (T) -> R): Maybe<List<R>> = map { it.map(mapper) }

/**
 * Same as [mapIterable] but saves resulting values into a [MutableCollection] returned by [collectionSupplier].
 */
fun <T, R, C : MutableCollection<R>> Maybe<Iterable<T>>.mapIterableTo(collectionSupplier: () -> C, mapper: (T) -> R): Maybe<C> =
    map { it.mapTo(collectionSupplier(), mapper) }
