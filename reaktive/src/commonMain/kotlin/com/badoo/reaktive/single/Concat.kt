package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.concat
import com.badoo.reaktive.observable.concatMap

fun <T> Iterable<Single<T>>.concat(): Observable<T> =
    asObservable()
        .concatMap { it.asObservable() }

fun <T> concat(vararg sources: Single<T>): Observable<T> =
    sources
        .map(Single<T>::asObservable)
        .concat()
