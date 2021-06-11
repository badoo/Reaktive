package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.repeat

/**
 * When the [Completable] signals `onComplete`, re-subscribes to the [Completable], [count] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#repeat-long-).
 */
fun Completable.repeat(count: Int = -1): Completable =
    asObservable<Nothing>()
        .repeat(count = count)
        .asCompletable()
