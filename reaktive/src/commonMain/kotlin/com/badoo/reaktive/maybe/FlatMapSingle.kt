package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asMaybe
import com.badoo.reaktive.single.map

fun <T, R> Maybe<T>.flatMapSingle(mapper: (T) -> Single<R>): Maybe<R> =
    flatMap {
        mapper(it).asMaybe()
    }

fun <T, U, R> Maybe<T>.flatMapSingle(
    mapper: (T) -> Single<U>,
    resultSelector: (T, U) -> R
): Maybe<R> = flatMapSingle { t ->
    mapper(t).map { u -> resultSelector(t, u) }
}