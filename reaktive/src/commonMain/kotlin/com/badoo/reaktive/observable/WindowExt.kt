package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.completableOfEmpty
import com.badoo.reaktive.completable.delay
import com.badoo.reaktive.maybe.delay
import com.badoo.reaktive.maybe.maybeOf
import com.badoo.reaktive.scheduler.Scheduler

/**
 * See the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-long-java.util.concurrent.TimeUnit-long-boolean-).
 */
fun <T> Observable<T>.window(
    timeSpanMillis: Long,
    scheduler: Scheduler,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<Observable<T>> {
    require(timeSpanMillis > 0L) { "Time span must be positive" }
    require(limit > 0) { "Limit must be positive" }

    return window(
        boundaries = WindowBoundary(limit = limit, restartOnLimit = restartOnLimit)
            .toObservable()
            .repeatWhen { maybeOf(Unit).delay(timeSpanMillis, scheduler) },
        isExclusive = true
    )
}

/**
 * See the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#window-long-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun <T> Observable<T>.window(
    timeSpanMillis: Long,
    timeSkipMillis: Long,
    scheduler: Scheduler,
    limit: Long = Long.MAX_VALUE,
    restartOnLimit: Boolean = false
): Observable<Observable<T>> {
    require(timeSkipMillis > 0L) { "Time skip must be positive" }
    require(timeSpanMillis > 0L) { "Time span must be positive" }
    require(limit > 0) { "Limit must be positive" }

    return window(
        WindowBoundary(
            closingSignal = completableOfEmpty().delay(delayMillis = timeSpanMillis, scheduler = scheduler),
            limit = limit,
            restartOnLimit = restartOnLimit
        )
            .toObservable()
            .repeatWhen { maybeOf(Unit).delay(timeSkipMillis, scheduler) }
    )
}
