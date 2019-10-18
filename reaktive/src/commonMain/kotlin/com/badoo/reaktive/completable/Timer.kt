package com.badoo.reaktive.completable

import com.badoo.reaktive.scheduler.Scheduler

fun completableTimer(delayMillis: Long, scheduler: Scheduler): Completable =
    completable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delayMillis, emitter::onComplete)
    }
    