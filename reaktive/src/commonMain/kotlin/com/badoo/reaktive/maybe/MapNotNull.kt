package com.badoo.reaktive.maybe

fun <T, R : Any> Maybe<T>.mapNotNull(mapper: (T) -> R?): Maybe<R> = map(mapper).notNull()
