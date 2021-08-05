package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe

/**
 * Same as [Single.map] but returns a [Maybe] which emits the resulting value only if it is not `null`, otherwise completes.
 */
fun <T, R : Any> Single<T>.mapNotNull(mapper: (T) -> R?): Maybe<R> = map(mapper).notNull()
