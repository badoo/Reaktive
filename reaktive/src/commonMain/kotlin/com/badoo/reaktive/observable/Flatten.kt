package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import kotlin.jvm.JvmName

/**
 * Concatenates all emitted [Iterable]s of elements. See [concatMap] for more information.
 */
fun <T> Observable<Iterable<T>>.flatten(): Observable<T> = concatMap(Iterable<T>::asObservable)

/**
 * Concatenates all emitted [Observable]s. See [concatMap] for more information.
 */
@JvmName("flattenObservable")
fun <T> Observable<Observable<T>>.flatten(): Observable<T> = concatMap { it }

/**
 * Concatenates all emitted [Single]s. See [concatMap] for more information.
 */
@JvmName("flattenSingle")
fun <T> Observable<Single<T>>.flatten(): Observable<T> = concatMap(Single<T>::asObservable)

/**
 * Concatenates all emitted [Maybe]s. See [concatMap] for more information.
 */
@JvmName("flattenMaybe")
fun <T> Observable<Maybe<T>>.flatten(): Observable<T> = concatMap(Maybe<T>::asObservable)
