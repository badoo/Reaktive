package com.badoo.reaktive.observable

inline fun <reified T> Observable<*>.ofType(): Observable<T> =
    filter { it is T }
        .map { it as T }