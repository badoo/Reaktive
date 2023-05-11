package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler
import kotlin.time.Duration

/**
 * Signals `onNext` with [delay] value after the given [delay] delay, and then completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#timer-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun observableTimer(delay: Duration, scheduler: Scheduler): Observable<Duration> =
    observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delay = delay) {
            emitter.onNext(delay)
            emitter.onComplete()
        }
    }
