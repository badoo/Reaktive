package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single

fun <T> Maybe<T>.defaultIfEmpty(value: T): Single<T> = asSingle(value)
