package com.badoo.reaktive.scheduler

internal actual class BufferedExecutor<in T> actual constructor(
    private val executor: Scheduler.Executor,
    private val onNext: (T) -> Unit
) {

    private val queue: MutableList<T> = ArrayList()
    private var isDraining = false
    private val drainFunction = ::drain

    actual fun submit(value: T) {
        queue += value
        if (!isDraining) {
            isDraining = true
            executor.submit(0, drainFunction)
        }
    }

    private fun drain() {
        while (!executor.isDisposed) {
            if (queue.isNotEmpty()) {
                onNext(queue.removeAt(0))
            } else {
                isDraining = false
                return
            }
        }
    }
}
