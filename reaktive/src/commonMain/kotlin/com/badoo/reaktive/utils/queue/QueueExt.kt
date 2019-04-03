package com.badoo.reaktive.utils.queue

internal val Queue<*>.isEmpty: Boolean get() = size == 0

internal val Queue<*>.isNotEmpty: Boolean get() = size > 0

internal fun <T> Queue<T>.take(): T =
    if (isEmpty) {
        throw NoSuchElementException("Queue is empty")
    } else {
        @Suppress("UNCHECKED_CAST")
        poll() as T
    }
