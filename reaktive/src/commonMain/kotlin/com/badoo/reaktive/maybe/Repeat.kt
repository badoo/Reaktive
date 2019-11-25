package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeat

fun <T> Maybe<T>.repeat(count: Int = -1): Observable<T> = asObservable().repeat(count = count)
