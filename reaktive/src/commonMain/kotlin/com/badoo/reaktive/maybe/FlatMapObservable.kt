package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.flatMap

fun <T, R> Maybe<T>.flatMapObservable(mapper: (T) -> Observable<R>): Observable<R> =
    asObservable().flatMap(mapper)