package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.map

inline fun <reified T> Single<*>.ofType(): Maybe<T> =
    filter { it is T }
        .map { it as T }
        