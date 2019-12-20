@file:Suppress("ForbiddenComment")

package com.badoo.reaktive.utils.queue

internal actual class PriorityQueue<T> actual constructor(
    private val comparator: Comparator<in T>
) : Queue<T> {

    private val list: MutableList<T> = ArrayList()
    override val peek: T? get() = list.getOrNull(0)
    override val size: Int get() = list.size
    override val isEmpty: Boolean get() = list.isEmpty()

    override fun offer(item: T) {
        list.add(item)
        list.sortWith(comparator) // TODO: Optimise later
    }

    override fun poll(): T? = if (list.isEmpty()) null else list.removeAt(0)

    override fun clear() {
        list.clear()
    }

    override fun iterator(): Iterator<T> = list.iterator()
}
