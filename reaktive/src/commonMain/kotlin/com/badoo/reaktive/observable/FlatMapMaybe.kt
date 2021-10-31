package com.badoo.reaktive.observable

import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.asObservable
import com.badoo.reaktive.maybe.map

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Maybe].
 * Emits elements from inner [Maybe]s. The maximum number of concurrently subscribed inner [Maybe]s is
 * determined by the [maxConcurrency] argument.
 *
 * By default, all inner [Maybe]s are subscribed concurrently without any limits.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.flatMapMaybe(maxConcurrency: Int = Int.MAX_VALUE, mapper: (T) -> Maybe<R>): Observable<R> =
    flatMap(maxConcurrency = maxConcurrency) { mapper(it).asObservable() }

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Maybe].
 * For an element [U] emitted by an inner [Maybe], calls [resultSelector] with the original source element [T]
 * and the inner element [U], and emits the result element [R]. The maximum number of concurrently subscribed inner [Maybe]s is
 * determined by the [maxConcurrency] argument.
 *
 * By default, all inner [Maybe]s are subscribed concurrently without any limits.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, U, R> Observable<T>.flatMapMaybe(maxConcurrency: Int = Int.MAX_VALUE, mapper: (T) -> Maybe<U>, resultSelector: (T, U) -> R): Observable<R> =
    flatMapMaybe(maxConcurrency = maxConcurrency) { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
