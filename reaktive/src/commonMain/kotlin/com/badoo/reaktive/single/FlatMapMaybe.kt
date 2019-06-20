package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.flatMap

fun <T, R> Single<T>.flatMapMaybe(mapper: (T) -> Maybe<R>): Maybe<R> =
    asMaybe().flatMap(mapper)