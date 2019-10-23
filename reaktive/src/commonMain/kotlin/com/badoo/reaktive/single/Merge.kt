package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.flatMapSingle
import com.badoo.reaktive.observable.merge

fun <T> Iterable<Single<T>>.merge(): Observable<T> =
    asObservable()
        .flatMapSingle { it }

fun <T> merge(vararg sources: Single<T>): Observable<T> =
    sources
        .map(Single<T>::asObservable)
        .merge()
