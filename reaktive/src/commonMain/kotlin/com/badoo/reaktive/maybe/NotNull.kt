package com.badoo.reaktive.maybe

fun <T : Any> Maybe<T?>.notNull(): Maybe<T> =
    transform { value, onSuccess, onComplete ->
        if (value != null) {
            onSuccess(value)
        } else {
            onComplete()
        }
    }