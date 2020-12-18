package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concat
import com.badoo.reaktive.observable.concatMap

fun <T> Iterable<Maybe<T>>.concat(): Observable<T> =
    asObservable()
        .concatMap { it.asObservable() }

fun <T> concat(vararg sources: Maybe<T>): Observable<T> =
    sources
        .map(Maybe<T>::asObservable)
        .concat()
