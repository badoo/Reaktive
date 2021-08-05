package com.badoo.reaktive.maybe

/**
 * Same as [Maybe.map] but returns a [Maybe] which emits the resulting value only if it is not `null`, otherwise completes.
 */
fun <T, R : Any> Maybe<T>.mapNotNull(mapper: (T) -> R?): Maybe<R> = map(mapper).notNull()
