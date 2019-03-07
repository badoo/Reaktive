package com.badoo.reaktive.observable

fun <T : Any> Observable<T?>.notNull(): Observable<T> =
    transform { value, onNext ->
        if (value != null) {
            onNext(value)
        }
    }