package com.badoo.reaktive.single

import com.badoo.reaktive.scheduler.Scheduler

fun singleTimer(delayMillis: Long, scheduler: Scheduler): Single<Long> =
    single { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delayMillis) { emitter.onSuccess(delayMillis) }
    }
