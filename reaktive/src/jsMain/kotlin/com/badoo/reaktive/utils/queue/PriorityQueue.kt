package com.badoo.reaktive.utils.queue

internal class PriorityQueue<T>(
    private val comparator: Comparator<in T>
) : Queue<T> {

    private val list: MutableList<T> = ArrayList()
    override val peek: T? get() = list.getOrNull(0)
    override val size: Int get() = list.size

    override fun offer(item: T) {
        list.add(item)
        list.sortWith(comparator) // TODO: Optimise later
    }

    override fun poll(): T? = if (list.isNotEmpty()) list.removeAt(0) else null

    override fun clear() {
        list.clear()
    }
}