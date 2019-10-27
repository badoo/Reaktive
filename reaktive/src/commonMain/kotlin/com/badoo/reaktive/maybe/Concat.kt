package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.concat

fun <T> Iterable<Maybe<T>>.concat(): Observable<T> =
    map(Maybe<T>::asObservable)
        .concat()

fun <T> concat(vararg sources: Maybe<T>): Observable<T> =
    sources
        .map(Maybe<T>::asObservable)
        .concat()
