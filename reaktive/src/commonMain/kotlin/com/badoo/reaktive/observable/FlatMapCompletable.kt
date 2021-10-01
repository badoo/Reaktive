package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.asObservable

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Completable].
 * All inner [Completable]s are subscribed concurrently without any limits.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMapCompletable-io.reactivex.functions.Function-).
 */
fun <T> Observable<T>.flatMapCompletable(mapper: (T) -> Completable): Completable =
    flatMapCompletable(maxConcurrency = Int.MAX_VALUE, mapper = mapper)

/**
 * Calls the [mapper] for each element emitted by the [Observable] and subscribes to the returned inner [Completable].
 * The maximum number of concurrently subscribed inner [Completable]s is determined by the [maxConcurrency] argument.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#flatMapCompletable-io.reactivex.functions.Function-).
 */
fun <T> Observable<T>.flatMapCompletable(maxConcurrency: Int, mapper: (T) -> Completable): Completable =
    flatMap(maxConcurrency = maxConcurrency) { mapper(it).asObservable<Nothing>() }
        .asCompletable()
