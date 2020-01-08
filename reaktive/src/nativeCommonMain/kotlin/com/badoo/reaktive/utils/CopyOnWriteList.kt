package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicList
import com.badoo.reaktive.utils.atomic.add
import com.badoo.reaktive.utils.atomic.clear
import com.badoo.reaktive.utils.atomic.get
import com.badoo.reaktive.utils.atomic.isEmpty
import com.badoo.reaktive.utils.atomic.plusAssign
import com.badoo.reaktive.utils.atomic.remove
import com.badoo.reaktive.utils.atomic.removeAt
import com.badoo.reaktive.utils.atomic.set
import com.badoo.reaktive.utils.atomic.size

internal class CopyOnWriteList<T>(initialList: List<T> = emptyList()) : MutableList<T> {

    private val delegate = AtomicList(initialList)

    override val size: Int get() = delegate.size

    override fun contains(element: T): Boolean = delegate.value.contains(element)

    override fun containsAll(elements: Collection<T>): Boolean = delegate.value.containsAll(elements)

    override fun get(index: Int): T = delegate[index]

    override fun indexOf(element: T): Int = delegate.value.indexOf(element)

    override fun isEmpty(): Boolean = delegate.isEmpty

    override fun iterator(): MutableIterator<T> = MutableListIteratorImpl(this, 0)

    override fun lastIndexOf(element: T): Int = delegate.value.lastIndexOf(element)

    override fun add(element: T): Boolean {
        delegate += element

        return true
    }

    override fun add(index: Int, element: T) {
        delegate.add(index, element)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        if (elements.isEmpty()) {
            return false
        }

        val oldList = delegate.value
        val newList = ArrayList<T>(oldList.size + elements.size)

        for (i in 0 until index) {
            newList.add(oldList[i])
        }

        newList.addAll(elements)

        for (i in index until oldList.size) {
            newList.add(oldList[i])
        }

        delegate.value = newList

        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        if (elements.isEmpty()) {
            return false
        }

        delegate.value = delegate.value + elements

        return true
    }

    override fun clear() {
        delegate.clear()
    }

    override fun listIterator(): MutableListIterator<T> = MutableListIteratorImpl(this, 0)

    override fun listIterator(index: Int): MutableListIterator<T> = MutableListIteratorImpl(this, index)

    override fun remove(element: T): Boolean = delegate.remove(element)

    override fun removeAll(elements: Collection<T>): Boolean {
        val oldList = delegate.value
        val newList = oldList - elements
        delegate.value = newList

        return newList.size < oldList.size
    }

    override fun removeAt(index: Int): T = delegate.removeAt(index)

    override fun retainAll(elements: Collection<T>): Boolean {
        val oldList = delegate.value
        val newList = oldList.filter(elements::contains)
        delegate.value = newList

        return newList.size < oldList.size
    }

    override fun set(index: Int, element: T): T = delegate.set(index, element)

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> {
        throw NotImplementedError() // It's tricky and we don't need it at the moment
    }

    override fun equals(other: Any?): Boolean = delegate.value == other

    override fun hashCode(): Int = delegate.value.hashCode()

    private inner class MutableListIteratorImpl<T>(
        private val list: MutableList<T>,
        private var index: Int
    ) : MutableListIterator<T> {
        private var lastIndex = -1

        override fun hasPrevious(): Boolean = index > 0
        override fun hasNext(): Boolean = index < list.size

        override fun previousIndex(): Int = index - 1
        override fun nextIndex(): Int = index

        override fun previous(): T {
            if (index <= 0) {
                throw NoSuchElementException()
            }

            lastIndex = --index

            return list[lastIndex]
        }

        override fun next(): T {
            if (index >= list.size) {
                throw NoSuchElementException()
            }

            lastIndex = index++

            return list[lastIndex]
        }

        override fun set(element: T) {
            list[lastIndex] = element
        }

        override fun add(element: T) {
            list.add(index++, element)
            lastIndex = -1
        }

        override fun remove() {
            check(lastIndex != -1) { "Call next() or previous() before removing element from the iterator." }

            list.removeAt(lastIndex)
            index = lastIndex
            lastIndex = -1
        }
    }
}
