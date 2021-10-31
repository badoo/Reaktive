package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicLong

/**
 * Returns an [Observable] that emits `0L` after [startDelayMillis] and
 * ever increasing numbers after each period of time specified by [periodMillis], on a specified [Scheduler].
 *
 * Default start delay is equal to the specified period
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#interval-long-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun observableInterval(periodMillis: Long, startDelayMillis: Long = periodMillis, scheduler: Scheduler): Observable<Long> =
    observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)

        val count = AtomicLong(-1L)
        executor.submitRepeating(startDelayMillis, periodMillis) {
            emitter.onNext(count.addAndGet(1L))
        }
    }
