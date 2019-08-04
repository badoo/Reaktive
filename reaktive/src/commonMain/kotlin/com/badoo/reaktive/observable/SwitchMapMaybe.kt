package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.map

fun <T, R> Observable<T>.switchMapMaybe(mapper: (T) -> Maybe<R>): Observable<R> =
    switchMap {
        mapper(it).asObservable()
    }

fun <T, U, R> Observable<T>.switchMapMaybe(
    mapper: (T) -> Maybe<U>,
    resultSelector: (T, U) -> R
): Observable<R> =
    switchMapMaybe { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }