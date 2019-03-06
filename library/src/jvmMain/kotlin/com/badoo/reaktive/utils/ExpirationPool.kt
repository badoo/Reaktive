package com.badoo.reaktive.utils

import java.util.concurrent.BlockingQueue
import java.util.concurrent.DelayQueue
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

internal class ExpirationPool<T> {

    private val queue = DelayQueue<Entry<T>>()
    private val daemon = Daemon(queue)

    init {
        daemon.start()
    }

    fun acquire(): T? =
        queue
            .peek()
            ?.also { queue.remove(it) }
            ?.item

    fun release(item: T, timeoutMillis: Long) {
        queue.offer(
            Entry(
                item,
                System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMillis)
            )
        )
    }

    fun destroy() {
        daemon.interrupt()
    }

    private companion object {
        private val sequencer = AtomicLong()
    }

    private class Daemon(
        private val queue: BlockingQueue<*>
    ) : Thread() {
        init {
            isDaemon = true
        }

        override fun run() {
            super.run()

            while (!isInterrupted) {
                try {
                    queue.take()
                } catch (e: InterruptedException) {
                    interrupt()
                    break
                }
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
    }
}