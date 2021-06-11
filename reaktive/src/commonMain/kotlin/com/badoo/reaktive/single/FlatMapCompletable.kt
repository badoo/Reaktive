package com.badoo.reaktive.single

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asSingle

/**
 * Calls the [mapper] with the value emitted by the [Single] and subscribes to the returned inner [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#flatMapCompletable-io.reactivex.functions.Function-).
 */
fun <T> Single<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    flatMap {
        mapper(it).asSingle(Unit)
    }
        .asCompletable()
