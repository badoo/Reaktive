package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable

fun <T> Maybe<Iterable<T>>.flatten(): Observable<T> = flatMapObservable(Iterable<T>::asObservable)

fun <T> Maybe<Observable<T>>.flatten(): Observable<T> = flatMapObservable { it }