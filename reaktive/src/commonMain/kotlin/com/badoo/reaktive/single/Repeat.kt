package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeat

/**
 * When the [Single] signals `onSuccess`, re-subscribes to the [Single], [count] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#repeat-long-).
 */
fun <T> Single<T>.repeat(count: Int = -1): Observable<T> = asObservable().repeat(count = count)
