package com.badoo.reaktive.utils

internal actual class SharedList<T> actual constructor(initialCapacity: Int) : MutableList<T> {

    private val helper =
        MutableFreezableHelper<MutableList<T>, ArrayList<T>, CopyOnWriteList<T>>(
            mutableFactory = { ArrayList(initialCapacity) },
            freezableFactory = { CopyOnWriteList(it ?: emptyList()) }
        )

    private val delegate: MutableList<T> get() = helper.obj

    override val size: Int get() = delegate.size

    override fun contains(element: T): Boolean = delegate.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = delegate.containsAll(elements)

    override fun get(index: Int): T = delegate[index]

    override fun indexOf(element: T): Int = delegate.indexOf(element)

    override fun isEmpty(): Boolean = delegate.isEmpty()

    override fun iterator(): MutableIterator<T> = delegate.iterator()

    override fun lastIndexOf(element: T): Int = delegate.lastIndexOf(element)

    override fun add(element: T): Boolean = delegate.add(element)

    override fun add(index: Int, element: T) {
        delegate.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean = delegate.addAll(index, elements)

    override fun addAll(elements: Collection<T>): Boolean = delegate.addAll(elements)

    override fun clear() {
        delegate.clear()
    }

    override fun listIterator(): MutableListIterator<T> = delegate.listIterator()

    override fun listIterator(index: Int): MutableListIterator<T> = delegate.listIterator(index)

    override fun remove(element: T): Boolean = delegate.remove(element)

    override fun removeAll(elements: Collection<T>): Boolean = delegate.removeAll(elements)

    override fun removeAt(index: Int): T = delegate.removeAt(index)

    override fun retainAll(elements: Collection<T>): Boolean = delegate.retainAll(elements)

    override fun set(index: Int, element: T): T = delegate.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        throw NotImplementedError() // It's tricky and we don't need it at the moment
    }

    override fun equals(other: Any?): Boolean = delegate == other

    override fun hashCode(): Int = delegate.hashCode()
}
