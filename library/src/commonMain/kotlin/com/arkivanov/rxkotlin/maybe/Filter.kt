package com.arkivanov.rxkotlin.maybe

fun <T> Maybe<T>.filter(predicate: (T) -> Boolean): Maybe<T> =
    transform { value, onSuccess, onComplete ->
        if (predicate(value)) {
            onSuccess(value)
        } else {
            onComplete()
        }
    }