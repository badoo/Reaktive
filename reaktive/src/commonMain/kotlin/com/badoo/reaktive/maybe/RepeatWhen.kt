package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeatWhen

fun <T> Maybe<T>.repeatWhen(handler: (repeatNumber: Int) -> Maybe<*>): Observable<T> =
    asObservable().repeatWhen(handler)
