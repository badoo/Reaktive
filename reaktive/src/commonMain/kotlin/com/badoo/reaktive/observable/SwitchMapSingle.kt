package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.map

fun <T, R> Observable<T>.switchMapSingle(mapper: (T) -> Single<R>): Observable<R> =
    switchMap {
        mapper(it).asObservable()
    }

fun <T, U, R> Observable<T>.switchMapSingle(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Observable<R> =
    switchMapSingle { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
    