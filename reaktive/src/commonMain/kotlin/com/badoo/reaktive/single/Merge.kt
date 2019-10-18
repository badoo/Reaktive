package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.merge

fun <T> Iterable<Single<T>>.merge(): Observable<T> =
    map(Single<T>::asObservable)
        .merge()

fun <T> merge(vararg sources: Single<T>): Observable<T> =
    sources
        .map(Single<T>::asObservable)
        .merge()
        