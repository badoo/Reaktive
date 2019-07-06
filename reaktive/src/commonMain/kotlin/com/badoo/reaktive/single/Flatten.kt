package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable

fun <T> Single<Iterable<T>>.flatten(): Observable<T> = flatMapObservable(Iterable<T>::asObservable)

fun <T> Single<Observable<T>>.flatten(): Observable<T> = flatMapObservable { it }