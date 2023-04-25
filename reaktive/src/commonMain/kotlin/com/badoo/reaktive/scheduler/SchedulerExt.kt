package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.Disposable
import kotlin.time.Duration

fun Scheduler.submit(
    delay: Duration = Duration.ZERO,
    period: Duration = Duration.INFINITE,
    task: () -> Unit,
): Disposable {
    val executor = newExecutor()

    executor.submit(delay = delay, period = period) {
        task()
        executor.dispose()
    }

    return executor
}
