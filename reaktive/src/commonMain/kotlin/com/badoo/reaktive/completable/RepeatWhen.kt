package com.badoo.reaktive.completable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.repeatWhen

/**
 * When the [Completable] signals `onComplete`, re-subscribes to the [Completable] when the [Maybe] returned by the [handler] function emits a value.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#repeatWhen-io.reactivex.functions.Function-).
 */
fun Completable.repeatWhen(handler: (repeatNumber: Int) -> Maybe<*>): Completable =
    asObservable<Nothing>()
        .repeatWhen(handler)
        .asCompletable()
