package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.clock.Clock
import com.badoo.reaktive.utils.clock.DefaultClock
import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.queue.PriorityQueue
import kotlin.concurrent.AtomicLong
import kotlin.time.Duration

internal class DelayQueue<T : Any>(
    private val clock: Clock = DefaultClock,
) {

    private val lock = ConditionLock()
    private var queue: PriorityQueue<Holder<T>>? = PriorityQueue(HolderComparator)

    /**
     * Terminates the queue. Any currently waiting [take] methods will immediately return null.
     * All methods will do nothing.
     */
    fun terminate() {
        lock.synchronized {
            queue = null
            lock.signal()
        }
    }

    fun removeFirst(): T? =
        lock.synchronized {
            val queue = queue ?: return@synchronized null
            val holder = queue.poll()
            lock.signal()
            holder?.value
        }

    /**
     * Waits until an item will be available and then returns the item.
     * Immediately returns null when terminated.
     */
    @Suppress("NestedBlockDepth")
    fun take(): T? {
        lock.synchronized {
            while (true) {
                val queue = queue ?: return null
                val item: Holder<T>? = queue.peek()

                if (item == null) {
                    lock.await()
                } else {
                    val timeout = item.endTime - clock.uptime

                    if (!timeout.isPositive()) {
                        queue.poll()

                        return item.value
                    }

                    lock.await(timeout)
                }
            }
        }
    }

    fun offer(value: T, timeout: Duration) {
        offerAt(value, clock.uptime + timeout)
    }

    fun offerAt(value: T, time: Duration) {
        lock.synchronized {
            val queue = queue ?: return
            queue.offer(Holder(value, time))
            lock.signal()
        }
    }

    fun removeIf(predicate: (T) -> Boolean) {
        lock.synchronized {
            val oldQueue = queue?.takeUnless { it.isEmpty } ?: return
            val newQueue = PriorityQueue<Holder<T>>(HolderComparator)
            this.queue = newQueue

            oldQueue.forEach { holder ->
                if (!predicate(holder.value)) {
                    newQueue.offer(holder)
                }
            }
        }
    }

    private data class Holder<out T>(
        val value: T,
        val endTime: Duration,
    ) {
        val sequenceNumber = sequencer.addAndGet(1L)

        private companion object {
            private val sequencer = AtomicLong(0L)
        }
    }

    private object HolderComparator : Comparator<Holder<*>> {
        override fun compare(a: Holder<*>, b: Holder<*>): Int =
            if (a === b) {
                0
            } else {
                var diff = a.endTime.compareTo(b.endTime)
                if (diff == 0) {
                    diff = a.sequenceNumber.compareTo(b.sequenceNumber)
                }
                diff
            }
    }
}
