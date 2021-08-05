package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.map

/**
 * Returns [Maybe] that emits the success value of this [Single] if it is an instance of `T`,
 * otherwise completes.
 */
inline fun <reified T> Single<*>.ofType(): Maybe<T> =
    filter { it is T }
        .map { it as T }
