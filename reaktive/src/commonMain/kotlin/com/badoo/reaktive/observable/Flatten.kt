package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable

fun <T> Observable<Iterable<T>>.flatten(): Observable<T> = concatMap(Iterable<T>::asObservable)

fun <T> Observable<Observable<T>>.flatten(): Observable<T> = concatMap { it }

fun <T> Observable<Single<T>>.flatten(): Observable<T> = concatMap(Single<T>::asObservable)

fun <T> Observable<Maybe<T>>.flatten(): Observable<T> = concatMap(Maybe<T>::asObservable)
