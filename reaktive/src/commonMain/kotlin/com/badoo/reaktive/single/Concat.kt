package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.concat

fun <T> Iterable<Single<T>>.concat(): Observable<T> =
    map(Single<T>::asObservable)
        .concat()

fun <T> concat(vararg sources: Single<T>): Observable<T> =
    sources
        .map(Single<T>::asObservable)
        .concat()