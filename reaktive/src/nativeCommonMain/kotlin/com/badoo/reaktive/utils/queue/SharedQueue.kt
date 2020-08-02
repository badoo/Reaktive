package com.badoo.reaktive.utils.queue

import com.badoo.reaktive.utils.MutableFreezableHelper

@Suppress("EmptyDefaultConstructor")
internal actual class SharedQueue<T> actual constructor() : Queue<T> {

    private val helper =
        MutableFreezableHelper(
            mutableFactory = ::ArrayQueue,
            freezableFactory = { mutable: ArrayQueue<T>? ->
                LinkedFreezableQueue(mutable?.toList() ?: emptyList())
            }
        )

    private val delegate: Queue<T> get() = helper.obj
    override val peek: T? get() = delegate.peek
    override val size: Int get() = delegate.size
    override val isEmpty: Boolean get() = delegate.isEmpty

    override fun offer(item: T) {
        delegate.offer(item)
    }

    override fun poll(): T? = delegate.poll()

    override fun clear() {
        delegate.clear()
    }

    override fun iterator(): Iterator<T> = delegate.iterator()
}
