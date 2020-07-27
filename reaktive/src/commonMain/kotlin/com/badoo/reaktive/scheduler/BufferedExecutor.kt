package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.Disposable

internal expect class BufferedExecutor<in T>(
    executor: Scheduler.Executor,
    onNext: (T) -> Unit
) : Disposable {

    fun submit(value: T)
}
