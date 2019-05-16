package com.badoo.reaktive.utils.queue

internal class QueueImpl<T>(
    private val delegate: java.util.Queue<T>
) : Queue<T> {

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