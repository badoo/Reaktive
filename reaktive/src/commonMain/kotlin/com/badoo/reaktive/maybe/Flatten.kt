package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import kotlin.jvm.JvmName

/**
 * When the [Maybe] emits an [Iterable] of values, iterates over the [Iterable] and emits all values one by one as an [Observable].
 */
fun <T> Maybe<Iterable<T>>.flatten(): Observable<T> = flatMapObservable(Iterable<T>::asObservable)

/**
 * This is just a shortcut for [Maybe.flatMapObservable].
 */
@JvmName("flattenObservable")
fun <T> Maybe<Observable<T>>.flatten(): Observable<T> = flatMapObservable { it }
