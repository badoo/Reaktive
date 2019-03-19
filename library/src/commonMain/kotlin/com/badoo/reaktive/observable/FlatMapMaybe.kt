package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable

fun <T, R> Observable<T>.flatMapMaybe(mapper: (T) -> Maybe<R>): Observable<R> =
    flatMap {
        mapper(it).asObservable()
    }