package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe

fun <T, R : Any> Single<T>.mapNotNull(mapper: (T) -> R?): Maybe<R> = map(mapper).notNull()
