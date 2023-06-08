package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.changeAndGet
import com.badoo.reaktive.utils.lock.Lock

/*
 * Derived from RxJava SerializedEmitter.
 */
internal abstract class AbstractSerializer<T> : Lock(), Serializer<T> {

    private val counter = AtomicInt()

    protected abstract fun addLast(value: T)

    protected abstract fun clearQueue()

    protected abstract fun isEmpty(): Boolean

    protected abstract fun removeFirst(): T

    protected abstract fun onValue(value: T): Boolean

    override fun accept(value: T) {
        if (counter.compareAndSet(0, 1)) {
            if (!onValue(value)) {
                counter.value = -1
                return
            }

            if (counter.addAndGet(-1) == 0) {
                return
            }
        } else {
            if (counter.value < 0) {
                return
            }

            synchronized {
                addLast(value)
            }

            if (counter.changeAndGet { if (it >= 0) it + 1 else it } != 1) {
                return
            }
        }

        drainLoop()
    }

    override fun clear() {
        synchronized(::clearQueue)
    }

    private fun drainLoop() {
        var missed = 1
        while (true) {
            while (true) {
                var isEmpty = false
                var value: T? = null

                synchronized {
                    isEmpty = isEmpty()
                    if (!isEmpty) {
                        value = removeFirst()
                    }
                }

                if (isEmpty) {
                    break
                }

                @Suppress("UNCHECKED_CAST")
                if (!onValue(value as T)) {
                    counter.value = -1
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
