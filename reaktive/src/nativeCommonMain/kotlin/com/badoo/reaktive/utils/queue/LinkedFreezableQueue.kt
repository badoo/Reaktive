package com.badoo.reaktive.utils.queue

import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze

internal class LinkedFreezableQueue<T>(
    src: Collection<T>? = null
) : Queue<T> {

    private val head = AtomicReference<Node<T>?>(null)
    private val tail = AtomicReference<Node<T>?>(null)
    private val _size = AtomicInt(0)

    init {
        src?.forEach(::offer)
    }

    override val peek: T? get() = head.value?.item
    override val isEmpty: Boolean get() = head.value == null
    override val size: Int get() = _size.value

    override fun offer(item: T) {
        val node = Node(item).freeze()
        val tailNode: Node<T>? = tail.value
        if (tailNode == null) {
            head.value = node
            tail.value = node
        } else {
            tailNode.next.value = node
            tail.value = node
        }

        _size.value++
    }

    override fun poll(): T? {
        val headNode: Node<T> = head.value ?: return null

        val item = headNode.item
        val nextNode: Node<T>? = headNode.next.value
        head.value = nextNode
        if (nextNode == null) {
            tail.value = null
        }

        _size.value--

        return item
    }

    override fun clear() {
        head.value = null
        tail.value = null
        _size.value = 0
    }

    override fun iterator(): Iterator<T> =
        object : Iterator<T> {
            private var node: Node<T>? = head.value

            override fun hasNext(): Boolean = node != null

            override fun next(): T {
                val current = node ?: throw NoSuchElementException()
                node = current.next?.value

                return current.item
            }
        }

    private class Node<T>(
        val item: T
    ) {
        val next: AtomicReference<Node<T>?> = AtomicReference(null)
    }
}
