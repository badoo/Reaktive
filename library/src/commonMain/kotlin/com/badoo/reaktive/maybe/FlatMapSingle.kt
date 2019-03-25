package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asMaybe

fun <T, R> Maybe<T>.flatMapSingle(mapper: (T) -> Single<R>): Maybe<R> =
    flatMap {
        mapper(it).asMaybe()
    }