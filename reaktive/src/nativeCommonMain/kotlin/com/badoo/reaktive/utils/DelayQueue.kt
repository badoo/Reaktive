package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.queue.PriorityQueue
import kotlin.native.concurrent.AtomicLong
import kotlin.system.getTimeMillis
import kotlin.time.Duration.Companion.milliseconds

internal class DelayQueue<T : Any> {

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
                    val timeout = (item.endTimeMillis - getTimeMillis()).milliseconds

                    if (!timeout.isPositive()) {
                        queue.poll()

                        return item.value
                    }

                    lock.await(timeout)
                }
            }
        }
    }

    fun offer(value: T, timeoutMillis: Long) {
        offerAt(value, getTimeMillis() + timeoutMillis)
    }

    fun offerAt(value: T, timeMillis: Long) {
        lock.synchronized {
            val queue = queue ?: return
            queue.offer(Holder(value, timeMillis))
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
        val endTimeMillis: Long
    ) {
        val sequenceNumber = sequencer.addAndGet(1L)

        private companion object {
            private val sequencer = AtomicLong()
        }
    }

    private object HolderComparator : Comparator<Holder<*>> {
        override fun compare(a: Holder<*>, b: Holder<*>): Int =
            if (a === b) {
                0
            } else {
                var diff = a.endTimeMillis - b.endTimeMillis
                if (diff == 0L) {
                    diff = a.sequenceNumber - b.sequenceNumber
                }
                diff.coerceIn(-1, 1).toInt()
            }
    }
}
