package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized
import kotlin.native.concurrent.AtomicLong
import kotlin.system.getTimeMillis

internal class DelayQueue<T : Any> {

    private val lock = Lock()
    private val condition = lock.newCondition()
    private val queueRef: AtomicReference<List<Holder<T>>?> = AtomicReference(emptyList())

    /**
     * Terminates the queue. Any currently waiting [take] methods will immediately return null. All methods will do nothing.
     */
    fun terminate() {
        lock.synchronized {
            queueRef.value = null
            condition.signal()
        }
    }

    fun destroy() {
        condition.destroy()
        lock.destroy()
    }

    fun removeFirst(): T? =
        doSynchronizedIfNotTerminated { queue ->
            queue
                .firstOrNull()
                ?.value
                ?.also {
                    queueRef.value = queue.drop(1)
                    condition.signal()
                }
        }

    /**
     * Waits until an item will be available and then returns the item.
     * Immediately returns null when terminated.
     */
    fun take(): T? {
        lock.acquire()
        try {
            while (true) {
                val queue = queueRef.value ?: return null
                val item: Holder<T>? = queue.firstOrNull()

                if (item == null) {
                    condition.await()
                } else {
                    val timeoutNanos = (item.endTimeMillis - getTimeMillis()) * NANOS_IN_MILLIS

                    if (timeoutNanos <= 0L) {
                        queueRef.value = queue.drop(1)

                        return item.value
                    }

                    condition.await(timeoutNanos)
                }
            }
        } finally {
            lock.release()
        }
    }

    fun offer(value: T, timeoutMillis: Long) {
        offerAt(value, getTimeMillis() + timeoutMillis)
    }

    fun offerAt(value: T, timeMillis: Long) {
        val holder = Holder(value, timeMillis)
        doSynchronizedIfNotTerminated { queue ->
            queueRef.value = queue.plusSorted(holder, HolderComparator)
            condition.signal()
        }
    }

    fun clear() {
        doSynchronizedIfNotTerminated { queue ->
            if (queue.isNotEmpty()) {
                queueRef.value = emptyList()
                condition.signal()
            }
        }
    }

    fun removeIf(predicate: (T) -> Boolean) {
        doSynchronizedIfNotTerminated { queue ->
            if (queue.isNotEmpty()) {
                queueRef.value = queue.filterNot { predicate(it.value) }
                condition.signal()
            }
        }
    }

    private inline fun <R> doSynchronizedIfNotTerminated(block: (queue: List<Holder<T>>) -> R): R? =
        lock.synchronized {
            queueRef.value?.let(block)
        }

    private companion object {
        private const val NANOS_IN_MILLIS = 1_000_000L
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
