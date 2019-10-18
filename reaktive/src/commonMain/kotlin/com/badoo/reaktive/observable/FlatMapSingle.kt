package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.map

fun <T, R> Observable<T>.flatMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    flatMap {
        mapper(it).asObservable()
    }

fun <T, U, R> Observable<T>.flatMapSingle(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapSingle { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
    