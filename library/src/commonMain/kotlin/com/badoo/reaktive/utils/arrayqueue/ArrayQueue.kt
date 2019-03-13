package com.badoo.reaktive.utils.arrayqueue

import kotlin.math.abs

internal class ArrayQueue<T> {

    private var queue: Array<T?> = createArray(8)
    private var head = 0
    private var tail = 0
    private var isFull = false

    val peek: T? get() = queue[head]
    val size: Int get() = abs(tail - head)

    fun offer(item: T) {
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

    fun poll(): T? {
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