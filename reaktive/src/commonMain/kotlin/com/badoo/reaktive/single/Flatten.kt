package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable
import kotlin.jvm.JvmName

fun <T> Single<Iterable<T>>.flatten(): Observable<T> = flatMapObservable(Iterable<T>::asObservable)

@JvmName("flattenObservable")
fun <T> Single<Observable<T>>.flatten(): Observable<T> = flatMapObservable { it }
