package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.synchronizedCompat

internal actual class BufferedExecutor<in T> actual constructor(
    private val executor: Scheduler.Executor,
    private val onNext: (T) -> Unit
) {

    private val monitor = Any()
    private val queue = ArrayDeque<T>()
    private var isDraining = false
    private val drainFunction = ::drain

    actual fun submit(value: T) {
        synchronizedCompat(monitor) {
            queue.addLast(value)
            if (!isDraining) {
                isDraining = true
                executor.submit(0, drainFunction)
            }
        }
    }

    private fun drain() {
        while (!executor.isDisposed) {
            synchronizedCompat(monitor) {
                if (queue.isEmpty()) {
                    isDraining = false
                    return
                }

                queue.removeFirst()
            }
                .also(onNext)
        }
    }
}
