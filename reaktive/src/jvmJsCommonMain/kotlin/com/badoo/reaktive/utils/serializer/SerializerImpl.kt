package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.queue.Queue
import kotlin.jvm.Volatile

/*
 * Derived from RxJava SerializedEmitter
 */
internal abstract class SerializerImpl<in T>(
    private val queue: Queue<T>
) : Serializer<T> {

    @Volatile
    private var isDone = false
    private val counter = AtomicInt()

    override fun accept(value: T) {
        if (isDone) {
            return
        }

        if (counter.compareAndSet(0, 1)) {
            if (!onValue(value)) {
                isDone = true
                return
            }

            if (counter.addAndGet(-1) == 0) {
                return
            }
        } else {
            synchronized(queue) {
                queue.offer(value)
            }

            if (counter.addAndGet(1) > 1) {
                return
            }
        }

        drainLoop()
    }

    override fun clear() {
        synchronized(queue, queue::clear)
    }

    abstract fun onValue(value: T): Boolean

    private fun drainLoop() {
        var missed = 1
        while (true) {
            while (true) {
                var isEmpty = false
                var value: T? = null

                synchronized(queue) {
                    isEmpty = queue.isEmpty
                    if (!isEmpty) {
                        value = queue.poll()
                    }
                }

                if (isEmpty) {
                    break
                }

                @Suppress("UNCHECKED_CAST")
                if (!onValue(value as T)) {
                    isDone = true
                    return
                }
            }

            missed = counter.addAndGet(-missed)
            if (missed == 0) {
                break
            }
        }
    }
}
