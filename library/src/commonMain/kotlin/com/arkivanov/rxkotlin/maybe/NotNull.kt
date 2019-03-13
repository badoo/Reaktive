package com.arkivanov.rxkotlin.maybe

fun <T : Any> Maybe<T?>.notNull(): Maybe<T> =
    transform { value, onSuccess, onComplete ->
        if (value != null) {
            onSuccess(value)
        } else {
            onComplete()
        }
    }