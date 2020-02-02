package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.singleOf

fun <T> Maybe<T>.defaultIfEmpty(defaultValue: T): Single<T> = switchIfEmpty(singleOf(defaultValue))
