package com.badoo.reaktive.single

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.flatMap
import com.badoo.reaktive.maybe.map

/**
 * Calls the [mapper] with the value emitted by the [Single] and subscribes to the returned inner [Maybe].
 * Emits the value from the inner [Maybe] or completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#flatMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, R> Single<T>.flatMapMaybe(mapper: (T) -> Maybe<R>): Maybe<R> =
    asMaybe().flatMap(mapper)

/**
 * Calls the [mapper] with the value emitted by the [Single] and subscribes to the returned inner [Maybe].
 * When the inner [Maybe] emits, calls the [resultSelector] function with the original and the inner values and emits the result.
 * Completes if the inner [Maybe] completed.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#flatMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, U, R> Single<T>.flatMapMaybe(mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Maybe<R> =
    flatMapMaybe { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
