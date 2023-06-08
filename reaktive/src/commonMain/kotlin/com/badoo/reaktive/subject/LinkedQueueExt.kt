package com.badoo.reaktive.subject

internal fun <T> LinkedQueue.Node<T>.forEachAndGetLast(block: (T) -> Unit): LinkedQueue.Node<T> {
    var node = this

    while (true) {
        block(node.value)
        node = node.next ?: break
    }

    return node
}
