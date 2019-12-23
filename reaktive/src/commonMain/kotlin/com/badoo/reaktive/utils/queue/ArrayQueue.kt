package com.badoo.reaktive.utils.queue

internal class ArrayQueue<T> : Queue<T> {

    private var queue: Array<T?> = createArray(INITIAL_CAPACITY)
    private var head = 0
    private var tail = 0
    private var isFull = false
    override val peek: T? get() = queue[head]
    override val isEmpty: Boolean get() = (head == tail) && !isFull

    override val size: Int
        get() =
            when {
                isFull -> queue.size
                tail >= head -> tail - head
                else -> queue.size + tail - head
            }

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

    override fun iterator(): Iterator<T> =
        object : Iterator<T> {
            private var counter = size
            private var index = head
            private val lastIndex = queue.lastIndex

            override fun hasNext(): Boolean = counter > 0

            override fun next(): T {
                if (!hasNext()) {
                    throw NoSuchElementException()
                }
                @Suppress("UNCHECKED_CAST")
                val item = queue[index] as T

                counter--
                index++
                if (index > lastIndex) {
                    index = 0
                }

                return item
            }
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

    internal companion object {
        internal const val INITIAL_CAPACITY = 8

        @Suppress("UNCHECKED_CAST")
        private fun <T> createArray(size: Int): Array<T?> = arrayOfNulls<Any>(size) as Array<T?>
    }
}
