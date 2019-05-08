package com.badoo.reaktive.utils

import java.util.PriorityQueue
import java.util.Queue

/**
 * Similar to [Serializer][com.badoo.reaktive.utils.serializer.Serializer]
 */
internal abstract class PrioritySerializer<in T> {

    private val monitor = Any()
    private var queue: Queue<T>? = PriorityQueue()
    private var isDraining = false

    fun accept(value: T) {
        synchronized(monitor) {
            queue
                ?.apply { offer(value) }
                ?.takeUnless { isDraining }
                ?.also { isDraining = true }
        }
            ?.drain()
    }

    fun clear() {
        synchronized(monitor) {
            queue?.clear()
        }
    }

    private fun Queue<T>.drain() {
        while (true) {
            synchronized(monitor) {
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
                    synchronized(monitor) {
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

    protected abstract fun onValue(value: T): Boolean
}