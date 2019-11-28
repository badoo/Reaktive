package com.badoo.reaktive.single

fun <T, R> Single<Iterable<T>>.mapIterable(mapper: (T) -> R): Single<List<R>> = map { it.map(mapper) }

fun <T, R, C : MutableCollection<R>> Single<Iterable<T>>.mapIterableTo(collectionSupplier: () -> C, mapper: (T) -> R): Single<C> =
    map { it.mapTo(collectionSupplier(), mapper) }
