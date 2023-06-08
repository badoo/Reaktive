package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeat

/**
 * When the [Maybe] signals `onSuccess` or `onComplete`, re-subscribes to the [Maybe], [times] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#repeat-long-).
 */
fun <T> Maybe<T>.repeat(times: Long = Long.MAX_VALUE): Observable<T> = asObservable().repeat(times = times)
