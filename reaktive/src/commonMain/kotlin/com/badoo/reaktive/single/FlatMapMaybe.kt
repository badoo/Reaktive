package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.flatMap
import com.badoo.reaktive.maybe.map

fun <T, R> Single<T>.flatMapMaybe(mapper: (T) -> Maybe<R>): Maybe<R> =
    asMaybe().flatMap(mapper)

fun <T, U, R> Single<T>.flatMapMaybe(mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Maybe<R> =
    flatMapMaybe { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }