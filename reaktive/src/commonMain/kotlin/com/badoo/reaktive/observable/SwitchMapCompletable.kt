package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asObservable

fun <T> Observable<T>.switchMapCompletable(mapper: (T) -> Completable): Completable =
    switchMap {
        mapper(it).asObservable<Nothing>()
    }
        .asCompletable()
