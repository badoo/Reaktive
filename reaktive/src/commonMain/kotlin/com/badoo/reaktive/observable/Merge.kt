package com.badoo.reaktive.observable

fun <T> Iterable<Observable<T>>.merge(): Observable<T> =
    asObservable()
        .flatMap { it }

fun <T> merge(vararg sources: Observable<T>): Observable<T> =
    sources
        .asIterable()
        .merge()
