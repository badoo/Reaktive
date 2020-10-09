package com.badoo.reaktive.observable

fun <T> Iterable<Observable<T>>.concat(): Observable<T> =
    asObservable()
        .concatMap { it }

fun <T> concat(vararg sources: Observable<T>): Observable<T> =
    sources
        .asList()
        .concat()
