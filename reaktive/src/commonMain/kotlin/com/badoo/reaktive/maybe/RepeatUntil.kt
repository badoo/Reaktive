package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeatUntil

fun <T> Maybe<T>.repeatUntil(predicate: () -> Boolean): Observable<T> = asObservable().repeatUntil(predicate)
