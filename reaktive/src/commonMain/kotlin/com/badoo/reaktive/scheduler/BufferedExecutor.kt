package com.badoo.reaktive.scheduler

internal expect class BufferedExecutor<in T>(
    executor: Scheduler.Executor,
    onNext: (T) -> Unit
) {

    fun submit(value: T)
}
