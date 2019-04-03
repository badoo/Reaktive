package com.badoo.reaktive.single

fun <T, R> Single<T>.map(mapper: (T) -> R): Single<R> =
    transform { value, onSuccess ->
        onSuccess(mapper(value))
    }