package com.badoo.reaktive.maybe

fun <T, R> Maybe<Iterable<T>>.mapIterable(mapper: (T) -> R): Maybe<List<R>> = map { it.map(mapper) }

fun <T, R, C : MutableCollection<R>> Maybe<Iterable<T>>.mapIterableTo(collectionSupplier: () -> C, mapper: (T) -> R): Maybe<C> =
    map { it.mapTo(collectionSupplier(), mapper) }
