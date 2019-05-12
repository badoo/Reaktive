package com.badoo.reaktive.utils.serializer

import java.util.ArrayDeque
import java.util.PriorityQueue
import java.util.Queue

internal actual abstract class Serializer<in T> actual constructor(
    comparator: Comparator<in T>?
) {

    private var queue: Queue<T>? = comparator?.let { PriorityQueue<T>(11, it) } ?: ArrayDeque<T>()
    private var isDraining = false

    actual fun accept(value: T) {
        synchronized(this) {
            queue
                ?.apply { offer(value) }
                ?.takeUnless { isDraining }
                ?.also { isDraining = true }
        }
            ?.drain()
    }

    actual fun clear() {
        synchronized(this) {
            queue?.clear()
        }
    }

    private fun Queue<T>.drain() {
        while (true) {
            synchronized(this) {
                if (isNotEmpty()) {
                    poll()!!
                } else {
                    onDrainFinished(false)
                    return
                }
            }
                .let(::onValue)
                .takeUnless { it }
                ?.also {
                    synchronized(this) {
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