package com.badoo.reaktive.scheduler

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.queue.LinkedFreezableQueue
import com.badoo.reaktive.utils.queue.Queue

internal actual class BufferedExecutor<in T> actual constructor(
    private val executor: Scheduler.Executor,
    private val onNext: (T) -> Unit
) : Disposable {

    private val lock = Lock()
    private val queue: Queue<T> = LinkedFreezableQueue()
    private val isDraining = AtomicBoolean(initialValue = false)
    private val drainFunction = ::drain

    private val _isDisposed = AtomicBoolean(initialValue = false)
    override val isDisposed: Boolean get() = _isDisposed.value

    override fun dispose() {
        _isDisposed.value = true
    }

    actual fun submit(value: T) {
        lock.synchronized {
            queue.offer(value)
            if (!isDraining.value) {
                isDraining.value = true
                executor.submit(0, drainFunction)
            }
        }
    }

    private fun drain() {
        while (!executor.isDisposed) {
            lock
                .synchronized {
                    if (queue.isEmpty) {
                        isDraining.value = false
                        return
                    }

                    @Suppress("UNCHECKED_CAST")
                    queue.poll() as T
                }
                .also(onNext)
        }
    }
}
