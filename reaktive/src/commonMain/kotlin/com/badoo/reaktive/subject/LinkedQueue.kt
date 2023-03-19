package com.badoo.reaktive.subject

import com.badoo.reaktive.utils.atomic.AtomicReference

internal class LinkedQueue<T>(
    private val limit: Int,
) {
    private val _head = AtomicReference<MutableNode<T>?>(null)
    val head: Node<T>? get() = _head.value // Can be read concurrently

    private var tail: MutableNode<T>? = null
    private var size: Int = 0

    fun addLast(value: T) {
        val node = MutableNode(value)

        if (size == 0) {
            _head.value = node
            tail = node
            size++
        } else {
            requireNotNull(tail).next = node
            tail = node

            if (size < limit) {
                size++
            } else {
                _head.value = requireNotNull(_head.value).next
            }
        }
    }

    interface Node<out T> {
        val value: T
        val next: Node<T>?
    }

    private class MutableNode<T>(override val value: T) : Node<T> {
        override var next: MutableNode<T>? = null
    }
}
