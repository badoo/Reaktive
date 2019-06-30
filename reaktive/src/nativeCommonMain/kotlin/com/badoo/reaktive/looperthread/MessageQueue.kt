package com.badoo.reaktive.looperthread

import com.badoo.reaktive.utils.Lock
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update
import com.badoo.reaktive.utils.synchronized
import kotlin.native.concurrent.AtomicLong
import kotlin.system.getTimeNanos

internal class MessageQueue {

    private val lock = Lock()
    private val condition = lock.newCondition()
    private val queue = AtomicReference(emptyList<Entry>(), true)

    fun offer(token: Any, startTimeNanos: Long, task: () -> Unit) {
        lock.synchronized {
            queue.update {
                it
                    .plus(Entry(token, startTimeNanos, task))
                    .sorted()
            }
            condition.signal()
        }
    }

    fun take(): () -> Unit {
        while (true) {
            lock.synchronized {
                var task: (() -> Unit)? = null

                queue.update {
                    if (it.isEmpty() || (it[0].startTimeNanos > getTimeNanos())) {
                        it
                    } else {
                        task = it[0].task
                        it.drop(1)
                    }
                }

                task?.also {
                    return it
                }


                val timeout = queue.value.getOrNull(0)?.startTimeNanos
                if (timeout != null) {
                    condition.await(timeout)
                } else {
                    condition.await()
                }
            }
        }
    }

    fun clear(token: Any) {
        lock.synchronized {
            queue.update { list ->
                list.filter { it.token != token }
            }
        }
    }

    fun clear() {
        lock.synchronized {
            queue.update { emptyList() }
        }
    }

    fun destroy() {
        clear()
        condition.destroy()
        lock.destroy()
    }

    private class Entry(
        val token: Any,
        val startTimeNanos: Long,
        val task: () -> Unit
    ) : Comparable<Entry> {
        private val sequenceNumber = sequencer.addAndGet(1L)

        override fun compareTo(other: Entry): Int =
            if (this === other) {
                0
            } else {
                val timeDiff = startTimeNanos - other.startTimeNanos
                when {
                    timeDiff < 0L -> -1
                    timeDiff > 0L -> 1
                    sequenceNumber < other.sequenceNumber -> -1
                    sequenceNumber > other.sequenceNumber -> 1
                    else -> 0
                }
            }

        private companion object {
            private val sequencer = AtomicLong()
        }
    }
}