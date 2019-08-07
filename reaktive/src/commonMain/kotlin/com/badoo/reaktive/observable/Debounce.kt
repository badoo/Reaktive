package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.completableTimer
import com.badoo.reaktive.scheduler.Scheduler

fun <T> Observable<T>.debounce(timeoutMillis: Long, scheduler: Scheduler): Observable<T> =
    debounce { completableTimer(timeoutMillis, scheduler) }