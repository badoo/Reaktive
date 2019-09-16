package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.queue.ArrayQueue
import com.badoo.reaktive.utils.queue.Queue
import com.badoo.reaktive.utils.queue.isNotEmpty
import com.badoo.reaktive.utils.queue.take

internal actual class BufferedExecutor<in T> actual constructor(
    private val executor: Scheduler.Executor,
    private val onNext: (T) -> Unit
) {

    private val monitor = Any()
    private val queue: Queue<T> = ArrayQueue()
    private var isDraining = false
    private val drainFunction = ::drain

    actual fun submit(value: T) {
        synchronized(monitor) {
            queue.offer(value)
            if (!isDraining) {
                isDraining = true
                executor.submit(0, drainFunction)
            }
        }
    }

    private fun drain() {
        while (!executor.isDisposed) {
            synchronized(monitor) {
                if (queue.isNotEmpty) {
                    queue.take()
                } else {
                    isDraining = false
                    return
                }
            }
                .also(onNext)
        }
    }
}
