package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.map

fun <T, R> Observable<T>.flatMapMaybe(mapper: (T) -> Maybe<R>): Observable<R> =
    flatMapMaybe(maxConcurrency = Int.MAX_VALUE, mapper = mapper)

fun <T, R> Observable<T>.flatMapMaybe(maxConcurrency: Int, mapper: (T) -> Maybe<R>): Observable<R> =
    flatMap(maxConcurrency = maxConcurrency) { mapper(it).asObservable() }

fun <T, U, R> Observable<T>.flatMapMaybe(mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapMaybe(maxConcurrency = Int.MAX_VALUE, mapper = mapper, resultSelector = resultSelector)

fun <T, U, R> Observable<T>.flatMapMaybe(maxConcurrency: Int, mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapMaybe(maxConcurrency = maxConcurrency) { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
