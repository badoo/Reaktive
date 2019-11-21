package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable

fun <T, R> Maybe<T>.flatMapIterable(transformer: (T) -> Iterable<R>): Observable<R> =
    flatMapObservable { transformer(it).asObservable() }
