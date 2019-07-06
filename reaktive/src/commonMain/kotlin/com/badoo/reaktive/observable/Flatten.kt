package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import kotlin.jvm.JvmName

fun <T> Observable<Iterable<T>>.flatten(): Observable<T> = concatMap(Iterable<T>::asObservable)

@JvmName("flattenObservable")
fun <T> Observable<Observable<T>>.flatten(): Observable<T> = concatMap { it }

@JvmName("flattenSingle")
fun <T> Observable<Single<T>>.flatten(): Observable<T> = concatMap(Single<T>::asObservable)

@JvmName("flattenMaybe")
fun <T> Observable<Maybe<T>>.flatten(): Observable<T> = concatMap(Maybe<T>::asObservable)
