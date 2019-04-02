package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.queue.ArrayQueue
import com.badoo.reaktive.utils.queue.Queue
import com.badoo.reaktive.utils.queue.isNotEmpty
import com.badoo.reaktive.utils.queue.take

/**
 * Serializes all calls to "accept" method and synchronously calls "onValue" method with corresponding values
 */
internal abstract class Serializer<in T>(queue: Queue<T> = ArrayQueue()) {

    private val lock = newLock()
    private var queue: Queue<T>? = queue
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

    fun clear() {
        lock.synchronized {
            queue?.clear()
        }
    }

    private fun Queue<T>.drain() {
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