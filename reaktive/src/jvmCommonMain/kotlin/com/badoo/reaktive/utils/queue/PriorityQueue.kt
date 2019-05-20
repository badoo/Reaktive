package com.badoo.reaktive.utils.queue

internal actual class PriorityQueue<T> actual constructor(
    comparator: Comparator<in T>
) : Queue<T> {

    private val delegate = java.util.PriorityQueue(11, comparator)
    override val peek: T? get() = delegate.peek()
    override val size: Int get() = delegate.size

    override fun offer(item: T) {
        delegate.offer(item)
    }

    override fun poll(): T? = delegate.poll()

    override fun clear() {
        delegate.clear()
    }
}
