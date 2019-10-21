package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable

fun <T, R> Observable<T>.concatMapMaybe(mapper: (T) -> Maybe<R>): Observable<R> =
    concatMap {
        mapper(it).asObservable()
    }
