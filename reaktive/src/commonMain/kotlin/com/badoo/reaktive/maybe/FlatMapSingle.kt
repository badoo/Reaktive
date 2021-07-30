package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asMaybe
import com.badoo.reaktive.single.map

/**
 * Calls the [mapper] with the value emitted by the [Maybe] and subscribes to the returned inner [Single].
 * Emits the value from the inner [Single] or completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#flatMapSingle-io.reactivex.functions.Function-).
 */
fun <T, R> Maybe<T>.flatMapSingle(mapper: (T) -> Single<R>): Maybe<R> =
    flatMap {
        mapper(it).asMaybe()
    }

/**
 * Calls the [mapper] with the value emitted by the [Maybe] and subscribes to the returned inner [Single].
 * When the inner [Single] emits, calls the [resultSelector] function with the original and the inner values and emits the result.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#flatMapSingle-io.reactivex.functions.Function-).
 */
fun <T, U, R> Maybe<T>.flatMapSingle(mapper: (T) -> Single<U>, resultSelector: (T, U) -> R): Maybe<R> =
    flatMapSingle { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
