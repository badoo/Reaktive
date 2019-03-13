package com.badoo.reaktive.observable

fun <T> Observable<T>.filter(predicate: (T) -> Boolean): Observable<T> =
    transform { value, onNext ->
        if (predicate(value)) {
            onNext(value)
        }
    }