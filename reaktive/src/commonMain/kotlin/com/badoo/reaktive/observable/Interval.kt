package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler
import kotlin.time.Duration

/**
 * Returns an [Observable] that emits `0L` after [startDelay] and
 * ever increasing numbers after each period of time specified by [period], on a specified [Scheduler].
 *
 * Default start delay is equal to the specified period
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#interval-long-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun observableInterval(period: Duration, startDelay: Duration = period, scheduler: Scheduler): Observable<Long> =
    observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)

        var count = 0L
        executor.submit(delay = startDelay, period = period) {
            emitter.onNext(count++)
        }
    }
