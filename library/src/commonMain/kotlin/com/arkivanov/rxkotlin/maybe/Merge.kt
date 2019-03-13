package com.arkivanov.rxkotlin.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.merge

fun <T> Iterable<Maybe<T>>.merge(): Observable<T> =
    map(Maybe<T>::toObservable)
        .merge()

fun <T> merge(vararg sources: Maybe<T>): Observable<T> =
    sources
        .map(Maybe<T>::toObservable)
        .merge()