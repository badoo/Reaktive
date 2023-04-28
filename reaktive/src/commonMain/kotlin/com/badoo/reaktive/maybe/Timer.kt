package com.badoo.reaktive.maybe

import com.badoo.reaktive.scheduler.Scheduler
import kotlin.time.Duration

/**
 * Signals `onSuccess` with [delay] value after the given [delay].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#timer-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun maybeTimer(delay: Duration, scheduler: Scheduler): Maybe<Duration> =
    maybe { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delay = delay) { emitter.onSuccess(delay) }
    }
