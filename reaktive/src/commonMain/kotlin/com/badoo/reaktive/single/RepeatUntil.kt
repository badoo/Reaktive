package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeatUntil

fun <T> Single<T>.repeatUntil(predicate: () -> Boolean): Observable<T> = asObservable().repeatUntil(predicate)
