package com.badoo.reaktive.observable

fun <T> Observable<T>.defaultIfEmpty(defaultValue: T): Observable<T> =
    switchIfEmpty(observableOf(defaultValue))