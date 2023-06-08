package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.repeat

/**
 * When the [Completable] signals `onComplete`, re-subscribes to the [Completable], [times] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#repeat-long-).
 */
fun Completable.repeat(times: Long = Long.MAX_VALUE): Completable =
    asObservable()
        .repeat(times = times)
        .asCompletable()
