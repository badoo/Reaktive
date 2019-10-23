package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import com.badoo.reaktive.observable.flatMapMaybe
import com.badoo.reaktive.observable.merge

fun <T> Iterable<Maybe<T>>.merge(): Observable<T> =
    asObservable()
        .flatMapMaybe { it }

fun <T> merge(vararg sources: Maybe<T>): Observable<T> =
    sources
        .map(Maybe<T>::asObservable)
        .merge()
