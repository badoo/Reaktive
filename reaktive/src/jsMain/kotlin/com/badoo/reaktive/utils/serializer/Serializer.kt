package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.queue.ArrayQueue
import com.badoo.reaktive.utils.queue.PriorityQueue
import com.badoo.reaktive.utils.queue.Queue
import com.badoo.reaktive.utils.queue.isNotEmpty
import com.badoo.reaktive.utils.queue.take

internal actual abstract class Serializer<in T> actual constructor(
    comparator: Comparator<in T>?
) {

    private var queue: Queue<T>? = comparator?.let(::PriorityQueue) ?: ArrayQueue()
    private var isDraining = false

    actual fun accept(value: T) {
        queue
            ?.apply { offer(value) }
            ?.takeUnless { isDraining }
            ?.also { isDraining = true }
            ?.drain()
    }

    actual fun clear() {
        queue?.clear()
    }

    private fun Queue<T>.drain() {
        while (true) {
            if (isNotEmpty) {
                take()
                    .let(::onValue)
                    .takeUnless { it }
                    ?.also {
                        onDrainFinished(true)
                        return
                    }
            } else {
                onDrainFinished(false)
                return
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