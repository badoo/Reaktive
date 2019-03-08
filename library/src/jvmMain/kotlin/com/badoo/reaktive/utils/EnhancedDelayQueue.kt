package com.badoo.reaktive.utils

import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * Reason: there is no atomic method in DelayQueue to remove head before timeout
 */
internal class EnhancedDelayQueue<T : Any> {

    private val queue = DelayQueue<Entry<T>>()
    private val set: MutableSet<Entry<T>> = Collections.newSetFromMap(ConcurrentHashMap<Entry<T>, Boolean>())

    fun poll(): T? =
        queue
            .peek()
            ?.takeIf(set::remove)
            ?.also { queue.remove(it) }
            ?.item

    fun offer(item: T, timeout: Long, timeUnit: TimeUnit) {
        val entry = Entry(item, System.nanoTime() + timeUnit.toNanos(timeout))
        set += entry
        queue.offer(entry)
    }

    fun take(): T {
        while (true) {
            queue
                .take()
                .takeIf(set::remove)
                ?.item
                ?.also {
                    return it
                }
        }
    }

    private class Entry<T>(
        val item: T,
        val expirationNanoTime: Long
    ) : Delayed {
        private val sequenceNumber = sequencer.getAndIncrement()

        override fun getDelay(timeUnit: TimeUnit): Long =
            timeUnit.convert(expirationNanoTime - System.nanoTime(), TimeUnit.NANOSECONDS)

        override fun compareTo(other: Delayed): Int =
            when {
                this === other -> 0
                other is Entry<*> -> {
                    val timeDiff = expirationNanoTime - other.expirationNanoTime
                    when {
                        timeDiff < 0L -> -1
                        timeDiff > 0L -> 1
                        sequenceNumber < other.sequenceNumber -> -1
                        sequenceNumber > other.sequenceNumber -> 1
                        else -> 0
                    }
                }
                else -> {
                    val delayDiff = getDelay(TimeUnit.NANOSECONDS) - other.getDelay(TimeUnit.NANOSECONDS)
                    when {
                        delayDiff < 0L -> -1
                        delayDiff > 0L -> 1
                        else -> 0
                    }
                }
            }

        private companion object {
            private val sequencer = AtomicLong()
        }
    }
}