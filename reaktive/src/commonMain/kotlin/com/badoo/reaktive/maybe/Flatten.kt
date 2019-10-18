package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import kotlin.jvm.JvmName

fun <T> Maybe<Iterable<T>>.flatten(): Observable<T> = flatMapObservable(Iterable<T>::asObservable)

@JvmName("flattenObservable")
fun <T> Maybe<Observable<T>>.flatten(): Observable<T> = flatMapObservable { it }
