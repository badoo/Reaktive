package com.badoo.reaktive.observable

fun <T, R> Observable<T>.map(mapper: (T) -> R): Observable<R> =
    transform { value, onNext ->
        onNext(mapper(value))
    }