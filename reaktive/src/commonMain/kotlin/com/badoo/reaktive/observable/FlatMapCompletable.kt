package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asObservable

fun <T> Observable<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    flatMap {
        mapper(it).asObservable<Nothing>()
    }
        .asCompletable()