package com.badoo.reaktive.single

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.maybe.flatMapCompletable

fun <T> Single<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    asMaybe()
        .flatMapCompletable(mapper)