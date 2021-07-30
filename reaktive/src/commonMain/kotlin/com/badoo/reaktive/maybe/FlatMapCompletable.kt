package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asMaybe

/**
 * Calls the [mapper] with the value emitted by the [Maybe] and subscribes to the returned inner [Completable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#flatMapCompletable-io.reactivex.functions.Function-).
 */
fun <T> Maybe<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    flatMap {
        mapper(it).asMaybe<Nothing>()
    }
        .asCompletable()
