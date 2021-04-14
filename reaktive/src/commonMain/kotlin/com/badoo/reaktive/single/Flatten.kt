package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import kotlin.jvm.JvmName

/**
 * When the [Single] emits an [Iterable] of values, iterates over the [Iterable] and emits all values one by one as an [Observable].
 */
fun <T> Single<Iterable<T>>.flatten(): Observable<T> = flatMapObservable(Iterable<T>::asObservable)

/**
 * This is just a shortcut for [Single.flatMapObservable].
 */
@JvmName("flattenObservable")
fun <T> Single<Observable<T>>.flatten(): Observable<T> = flatMapObservable { it }
