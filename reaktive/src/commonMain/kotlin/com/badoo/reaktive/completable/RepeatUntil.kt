package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.asCompletable
import com.badoo.reaktive.observable.repeatUntil

/**
 * When the [Completable] signals `onComplete`, re-subscribes to the [Completable] if the [predicate] function returns `false`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#repeatUntil-io.reactivex.functions.BooleanSupplier-).
 */
fun <T> Completable.repeatUntil(predicate: () -> Boolean): Completable =
    asObservable<Nothing>()
        .repeatUntil(predicate)
        .asCompletable()
