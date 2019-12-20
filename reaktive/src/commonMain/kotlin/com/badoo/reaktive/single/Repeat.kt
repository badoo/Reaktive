package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeat

fun <T> Single<T>.repeat(count: Int = -1): Observable<T> = asObservable().repeat(count = count)
