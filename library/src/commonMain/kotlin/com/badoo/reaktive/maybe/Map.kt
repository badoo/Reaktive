package com.badoo.reaktive.maybe

fun <T, R> Maybe<T>.map(mapper: (T) -> R): Maybe<R> =
    transform { value, onSuccess, _ ->
        onSuccess(mapper(value))
    }