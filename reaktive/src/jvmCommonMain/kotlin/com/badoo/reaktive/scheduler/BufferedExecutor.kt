package com.badoo.reaktive.scheduler

import java.util.ArrayDeque
import java.util.Queue

internal actual class BufferedExecutor<in T> actual constructor(
    private val executor: Scheduler.Executor,
    private val onNext: (T) -> Unit
) {

    private val queue: Queue<T> = ArrayDeque<T>()
    private var isDraining = false
    private val drainFunction = ::drain

    actual fun submit(value: T) {
        synchronized(this) {
            queue.offer(value)
            if (!isDraining) {
                isDraining = true
                executor.submit(0, drainFunction)
            }
        }
    }

    private fun drain() {
        while (!executor.isDisposed) {
            synchronized(this) {
                if (queue.isNotEmpty()) {
                    queue.poll()!!
                } else {
                    isDraining = false
                    return
                }
            }
                .also(onNext)
        }
    }
}
