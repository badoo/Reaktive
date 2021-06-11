package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.map

fun <T, R> Observable<T>.flatMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    flatMapSingle(maxConcurrency = Int.MAX_VALUE, mapper = mapper)

fun <T, R> Observable<T>.flatMapSingle(maxConcurrency: Int, mapper: (T) -> Single<R>): Observable<R> =
    flatMap(maxConcurrency = maxConcurrency) { mapper(it).asObservable() }

fun <T, U, R> Observable<T>.flatMapSingle(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapSingle(maxConcurrency = Int.MAX_VALUE, mapper = mapper, resultSelector = resultSelector)

fun <T, U, R> Observable<T>.flatMapSingle(maxConcurrency: Int, mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapSingle(maxConcurrency = maxConcurrency) { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
