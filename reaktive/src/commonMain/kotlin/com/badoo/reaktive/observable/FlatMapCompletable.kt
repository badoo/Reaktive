package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asObservable

fun <T> Observable<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    flatMapCompletable(maxConcurrency = Int.MAX_VALUE, mapper = mapper)

fun <T> Observable<T>.flatMapCompletable(maxConcurrency: Int, mapper: (T) -> Completable): Completable =
    flatMap(maxConcurrency = maxConcurrency) { mapper(it).asObservable<Nothing>() }
        .asCompletable()
