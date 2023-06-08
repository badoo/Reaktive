package com.badoo.reaktive.single

import com.badoo.reaktive.scheduler.Scheduler
import kotlin.time.Duration

/**
 * Signals `onSuccess` with [delay] value after the given [delay].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#timer-long-java.util.concurrent.TimeUnit-io.reactivex.Scheduler-).
 */
fun singleTimer(delay: Duration, scheduler: Scheduler): Single<Duration> =
    single { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delay = delay) { emitter.onSuccess(delay) }
    }
