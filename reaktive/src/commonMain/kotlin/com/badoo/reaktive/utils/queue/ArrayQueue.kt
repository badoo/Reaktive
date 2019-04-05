package com.badoo.reaktive.utils.queue

import kotlin.math.abs

internal class ArrayQueue<T> : Queue<T> {

    private var queue: Array<T?> = createArray(8)
    private var head = 0
    private var tail = 0
    private var isFull = false

    val peek: T? get() = queue[head]
    override val size: Int get() = abs(tail - head)

    override fun offer(item: T) {
        ensureCapacity()
        queue[tail] = item
        tail++
        if (tail > queue.lastIndex) {
            tail = 0
        }
        if (tail == head) {
            isFull = true
        }
    }

    override fun poll(): T? {
        val value = peek
        queue[head] = null
        if ((head != tail) || isFull) {
            head++
            isFull = false
            if (head > queue.lastIndex) {
                head = 0
            }
        }

        return value
    }

    override fun clear() {
        for (i in 0 until queue.size) {
            queue[i] = null
        }
        head = 0
        tail = 0
        isFull = false
    }

    private fun ensureCapacity() {
        if (!isFull) {
            return
        }

        isFull = false
        val arr = createArray<T>(queue.size shl 1)
        queue.copyInto(arr, 0, head, queue.size)
        queue.copyInto(arr, queue.size - head, 0, head)
        tail = queue.size
        head = 0
        queue = arr
    }

    private companion object {
        @Suppress("UNCHECKED_CAST")
        fun <T> createArray(size: Int): Array<T?> = arrayOfNulls<Any>(size) as Array<T?>
    }
}