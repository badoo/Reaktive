package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.flatMap
import com.badoo.reaktive.observable.map

fun <T, R> Maybe<T>.flatMapObservable(mapper: (T) -> Observable<R>): Observable<R> =
    asObservable().flatMap(mapper)

fun <T, U, R> Maybe<T>.flatMapObservable(
    mapper: (T) -> Observable<U>,
    resultSelector: (T, U) -> R
): Observable<R> = flatMapObservable { t ->
    mapper(t).map { u -> resultSelector(t, u) }
}