package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicLong

/**
 * Returns an [Observable] that emits `0L` after [startDelayMillis] and
 * ever increasing numbers after each period of time specified by [periodMillis], on a specified [Scheduler].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#interval-long-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun observableInterval(startDelayMillis: Long, periodMillis: Long, scheduler: Scheduler): Observable<Long> =
    observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)

        val count = AtomicLong(-1L)
        executor.submitRepeating(startDelayMillis, periodMillis) {
            emitter.onNext(count.addAndGet(1L))
        }
    }

/**
 * Returns an [Observable] that emits a sequential number every interval of time specified by [periodMillis],
 * on a specified [Scheduler].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#interval-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun observableInterval(periodMillis: Long, scheduler: Scheduler): Observable<Long> =
    observableInterval(periodMillis, periodMillis, scheduler)
