package com.badoo.reaktive.maybe

/**
 * Returns [Maybe] that emits the success value of this [Maybe] if it is an instance of `T`,
 * otherwise completes.
 */
inline fun <reified T> Maybe<*>.ofType(): Maybe<T> =
    filter { it is T }
        .map { it as T }
