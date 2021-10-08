package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler

/**
 * Signals `onNext` with [delayMillis] value after the given [delayMillis] delay, and then completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#timer-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun observableTimer(delayMillis: Long, scheduler: Scheduler): Observable<Long> =
    observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delayMillis) {
            emitter.onNext(delayMillis)
            emitter.onComplete()
        }
    }
