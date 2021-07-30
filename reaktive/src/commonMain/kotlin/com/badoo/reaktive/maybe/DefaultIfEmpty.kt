package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single

/**
 * Same as [Maybe.asSingle]
 */
fun <T> Maybe<T>.defaultIfEmpty(defaultValue: T): Single<T> = asSingle(defaultValue)
