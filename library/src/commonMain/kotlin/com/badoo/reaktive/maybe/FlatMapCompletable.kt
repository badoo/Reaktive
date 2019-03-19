package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asMaybe

fun <T> Maybe<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    flatMap {
        mapper(it).asMaybe<Nothing>()
    }
        .asCompletable()