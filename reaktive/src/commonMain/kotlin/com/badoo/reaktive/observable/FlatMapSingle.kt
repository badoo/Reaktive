package com.badoo.reaktive.observable

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asObservable
import com.badoo.reaktive.single.map

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Single].
 * Emits elements from inner [Single]s. The maximum number of concurrently subscribed inner [Single]s is
 * determined by the [maxConcurrency] argument.
 *
 * By default, all inner [Single]s are subscribed concurrently without any limits.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.flatMapSingle(maxConcurrency: Int = Int.MAX_VALUE, mapper: (T) -> Single<R>): Observable<R> =
    flatMap(maxConcurrency = maxConcurrency) { mapper(it).asObservable() }

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Single].
 * For an element [U] emitted by an inner [Single], calls [resultSelector] with the original source element [T]
 * and the inner element [U], and emits the result element [R]. The maximum number of concurrently subscribed inner [Single]s is
 * determined by the [maxConcurrency] argument.
 *
 * By default, all inner [Single]s are subscribed concurrently without any limits.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMapMaybe-io.reactivex.functions.Function-).
 */
fun <T, U, R> Observable<T>.flatMapSingle(
    maxConcurrency: Int = Int.MAX_VALUE,
    mapper: (T) -> Single<U>,
    resultSelector: (T, U) -> R
): Observable<R> =
    flatMapSingle(maxConcurrency = maxConcurrency) { t ->
        mapper(t).map { u -> resultSelector(t, u) }
    }
