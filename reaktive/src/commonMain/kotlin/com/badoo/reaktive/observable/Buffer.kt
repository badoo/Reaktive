package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.scheduler.Scheduler

/**
 * Returns an [Observable] that emits non-overlapping windows of elements it collects from the source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Observable.html#buffer-long-java.util.concurrent.TimeUnit-io.reactivex.rxjava3.core.Scheduler-int-io.reactivex.rxjava3.functions.Supplier-boolean-).
 */
fun <T> Observable<T>.buffer(
    spanMillis: Long,
    scheduler: Scheduler,
    limit: Int = Int.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<List<T>> =
    window(spanMillis = spanMillis, scheduler = scheduler, limit = limit.toLong(), restartOnLimit = restartOnLimit)
        .flatMapSingle { it.toList() }

/**
 * Returns an [Observable] that emits non-overlapping windows of elements it collects from the source [Observable].
 * Window boundaries are determined by the elements emitted by the specified [boundaries][boundaries] [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Observable.html#buffer-io.reactivex.rxjava3.core.ObservableSource-).
 */
fun <T> Observable<T>.buffer(
    boundaries: Observable<*>,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<List<T>> =
    window(boundaries = boundaries, limit = limit, restartOnLimit = restartOnLimit)
        .flatMapSingle { it.toList() }

/**
 * Returns an [Observable] that emits possibly overlapping windows of elements it collects from the source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Observable.html#buffer-long-long-java.util.concurrent.TimeUnit-io.reactivex.rxjava3.core.Scheduler-).
 */
fun <T> Observable<T>.buffer(
    spanMillis: Long,
    skipMillis: Long,
    scheduler: Scheduler,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<List<T>> =
    window(spanMillis = spanMillis, skipMillis = skipMillis, scheduler = scheduler, limit = limit, restartOnLimit = restartOnLimit)
        .flatMapSingle { it.toList() }

/**
 * Returns an [Observable] that emits possibly overlapping windows of elements it collects from the source [Observable].
 * Every new window is opened when the [opening][opening] [Observable] emits an element.
 * Each window is closed when the corresponding [Observable] returned by the [closing] function completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/3.x/javadoc/io/reactivex/rxjava3/core/Observable.html#buffer-io.reactivex.rxjava3.core.ObservableSource-io.reactivex.rxjava3.functions.Function-).
 */
fun <T, S> Observable<T>.buffer(
    opening: Observable<S>,
    closing: (S) -> Completable,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<List<T>> =
    window(opening = opening, closing = closing, limit = limit, restartOnLimit = restartOnLimit)
        .flatMapSingle { it.toList() }
