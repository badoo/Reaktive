package com.badoo.reaktive.maybe

import com.badoo.reaktive.scheduler.Scheduler

/**
 * Signals `onSuccess` with [delayMillis] value after the given [delayMillis] delay.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#timer-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun maybeTimer(delayMillis: Long, scheduler: Scheduler): Maybe<Long> =
    maybe { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delayMillis) { emitter.onSuccess(delayMillis) }
    }
