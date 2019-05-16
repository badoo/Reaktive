package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.Lock
import com.badoo.reaktive.utils.queue.ArrayQueue
import com.badoo.reaktive.utils.queue.PriorityQueue
import com.badoo.reaktive.utils.queue.Queue
import com.badoo.reaktive.utils.queue.isNotEmpty
import com.badoo.reaktive.utils.queue.take
import com.badoo.reaktive.utils.synchronized

internal actual abstract class Serializer<in T> actual constructor(
    comparator: Comparator<in T>?
) {

    private val lock = Lock()
    private var queue: Queue<T>? = comparator?.let(::PriorityQueue) ?: ArrayQueue()
    private var isDraining = false

    actual fun accept(value: T) {
        lock
            .synchronized {
                queue
                    ?.apply { offer(value) }
                    ?.takeUnless { isDraining }
                    ?.also { isDraining = true }
            }
            ?.drain()
    }

    actual fun clear() {
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

    protected actual abstract fun onValue(value: T): Boolean
}