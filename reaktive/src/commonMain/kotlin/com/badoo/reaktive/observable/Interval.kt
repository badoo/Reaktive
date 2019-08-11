package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.atomic.AtomicLong

fun observableInterval(startDelayMillis: Long, periodMillis: Long, scheduler: Scheduler): Observable<Long> =
    observable { emitter ->
        val executor = scheduler.newExecutor()
        emitter.setDisposable(executor)

        val count = AtomicLong()
        executor.submitRepeating(startDelayMillis, periodMillis) {
            emitter.onNext(count.value)
            count.addAndGet(1)
        }
    }

fun observableInterval(periodMillis: Long, scheduler: Scheduler): Observable<Long> =
    observableInterval(periodMillis, periodMillis, scheduler)