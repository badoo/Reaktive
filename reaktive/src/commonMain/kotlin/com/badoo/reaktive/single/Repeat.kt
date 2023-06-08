package com.badoo.reaktive.single

import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.repeat

/**
 * When the [Single] signals `onSuccess`, re-subscribes to the [Single], [times] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#repeat-long-).
 */
fun <T> Single<T>.repeat(times: Long = Long.MAX_VALUE): Observable<T> = asObservable().repeat(times = times)
