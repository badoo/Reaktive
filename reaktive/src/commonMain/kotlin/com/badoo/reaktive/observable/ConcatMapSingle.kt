package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable

fun <T, R> Observable<T>.concatMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    concatMap {
        mapper(it).asObservable()
    }
