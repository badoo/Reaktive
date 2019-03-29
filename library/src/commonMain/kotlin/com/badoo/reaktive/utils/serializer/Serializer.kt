package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.arrayqueue.ArrayQueue
import com.badoo.reaktive.utils.arrayqueue.isNotEmpty
import com.badoo.reaktive.utils.arrayqueue.take
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

/**
 * Serializes all calls to "accept" method and synchronously calls "onValue" method with corresponding values
 */
internal abstract class Serializer<in T> {

    private val lock = newLock()
    private var queue: ArrayQueue<T>? = ArrayQueue()
    private var isDraining = false

    /**
     * Either calls "onValue" with the specified value or queues the value.
     * This method is supposed to be called from multiple threads.
     * If there are no threads currently processing any value then this thread will process the specified value.
     * Otherwise value will be queued and processed later by existing thread.
     *
     * @param value the value
     */
    fun accept(value: T) {
        lock
            .synchronized {
                queue
                    ?.apply { offer(value) }
                    ?.takeUnless { isDraining }
                    ?.also { isDraining = true }
            }
            ?.drain()
    }

    private fun ArrayQueue<T>.drain() {
        while (true) {
            lock
                .synchronized {
                    if (isNotEmpty) {
                        take()
                    } else {
                        onDrainFinished(false)
                        return
                    }
                }
                .let(::onValue)
                .takeUnless { it }
                ?.also {
                    lock.synchronized {
                        onDrainFinished(true)
                        return
                    }
                }
        }
    }

    private fun onDrainFinished(terminate: Boolean) {
        isDraining = false
        if (terminate) {
            queue = null
        }
    }

    /**
     * Called synchronously for every value
     *
     * @param value a value
     * @return true if processing should continue, false otherwise
     */
    protected abstract fun onValue(value: T): Boolean
}