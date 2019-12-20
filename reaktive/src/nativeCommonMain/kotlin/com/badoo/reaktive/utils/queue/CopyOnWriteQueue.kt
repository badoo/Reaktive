package com.badoo.reaktive.utils.queue

import com.badoo.reaktive.utils.atomic.AtomicList
import com.badoo.reaktive.utils.atomic.clear
import com.badoo.reaktive.utils.atomic.firstOrNull
import com.badoo.reaktive.utils.atomic.isEmpty
import com.badoo.reaktive.utils.atomic.iterator
import com.badoo.reaktive.utils.atomic.plusAssign
import com.badoo.reaktive.utils.atomic.size

internal class CopyOnWriteQueue<T>(initialList: List<T> = emptyList()) : Queue<T> {

    private val delegate = AtomicList(initialList)
    override val peek: T? get() = delegate.firstOrNull()
    override val size: Int get() = delegate.size
    override val isEmpty: Boolean get() = delegate.isEmpty

    override fun offer(item: T) {
        delegate += item
    }

    override fun poll(): T? {
        val item: T? = delegate.firstOrNull()
        delegate.value = delegate.value.drop(1)

        return item
    }

    override fun clear() {
        delegate.clear()
    }

    override fun iterator(): Iterator<T> = delegate.iterator()
}
