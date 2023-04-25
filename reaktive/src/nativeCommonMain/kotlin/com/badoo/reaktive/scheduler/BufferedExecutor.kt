package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.lock.Lock

internal actual class BufferedExecutor<in T> actual constructor(
    private val executor: Scheduler.Executor,
    private val onNext: (T) -> Unit
) {

    private val lock = Lock()
    private val queue = ArrayDeque<T>()
    private var isDraining = false
    private val drainFunction = ::drain

    actual fun submit(value: T) {
        lock.synchronized {
            queue.addLast(value)
            if (!isDraining) {
                isDraining = true
                executor.submit(task = drainFunction)
            }
        }
    }

    private fun drain() {
        while (!executor.isDisposed) {
            lock
                .synchronized {
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
