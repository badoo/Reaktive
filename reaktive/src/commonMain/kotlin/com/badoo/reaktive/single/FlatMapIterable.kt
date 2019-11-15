package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.asObservable

fun <T, R> Single<T>.flatMapIterable(transformer: (T) -> Iterable<R>): Observable<R> =
    flatMapObservable { transformer(it).asObservable() }
