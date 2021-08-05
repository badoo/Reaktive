package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeat

/**
 * When the [Maybe] signals `onSuccess` or `onComplete`, re-subscribes to the [Maybe], [count] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#repeat-long-).
 */
fun <T> Maybe<T>.repeat(count: Int = -1): Observable<T> = asObservable().repeat(count = count)
