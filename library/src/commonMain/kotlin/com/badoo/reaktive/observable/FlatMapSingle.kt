package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable

fun <T, R> Observable<T>.flatMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    flatMap {
        mapper(it).asObservable()
    }