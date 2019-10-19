package com.badoo.reaktive.maybe

import com.badoo.reaktive.scheduler.Scheduler

fun maybeTimer(delayMillis: Long, scheduler: Scheduler): Maybe<Long> =
    maybe { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delayMillis) { emitter.onSuccess(delayMillis) }
    }
    