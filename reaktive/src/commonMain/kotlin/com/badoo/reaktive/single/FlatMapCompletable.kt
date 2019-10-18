package com.badoo.reaktive.single

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asSingle

fun <T> Single<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    flatMap {
        mapper(it).asSingle(Unit)
    }
        .asCompletable()
        