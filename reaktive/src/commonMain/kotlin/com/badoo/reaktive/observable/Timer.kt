package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler

fun observableTimer(delayMillis: Long, scheduler: Scheduler): Observable<Long> =
    observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)
        executor.submit(delayMillis) {
            emitter.onNext(0L)
            emitter.onComplete()
        }
    }