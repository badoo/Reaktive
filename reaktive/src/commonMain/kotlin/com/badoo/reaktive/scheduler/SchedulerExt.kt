package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.Disposable

fun Scheduler.submit(delayMillis: Long = 0L, task: () -> Unit): Disposable {
    val executor = newExecutor()

    executor.submit(delayMillis = delayMillis) {
        task()
        executor.dispose()
    }

    return executor
}

fun Scheduler.submitRepeating(startDelayMillis: Long = 0L, periodMillis: Long, task: () -> Unit): Disposable {
    val executor = newExecutor()

    executor.submitRepeating(startDelayMillis = startDelayMillis, periodMillis = periodMillis) {
        task()
        executor.dispose()
    }

    return executor
}
