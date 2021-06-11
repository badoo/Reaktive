package com.badoo.reaktive.single

import com.badoo.reaktive.scheduler.Scheduler

/**
 * Signals `onSuccess` with [delayMillis] value after the given [delayMillis] delay.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#timer-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun singleTimer(delayMillis: Long, scheduler: Scheduler): Single<Long> =
    single { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delayMillis) { emitter.onSuccess(delayMillis) }
    }
