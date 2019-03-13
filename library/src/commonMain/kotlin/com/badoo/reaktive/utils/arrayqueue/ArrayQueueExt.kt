package com.badoo.reaktive.utils.arrayqueue

internal val ArrayQueue<*>.isEmpty: Boolean get() = size == 0

internal val ArrayQueue<*>.isNotEmpty: Boolean get() = size > 0

internal fun <T> ArrayQueue<T>.take(): T =
    if (isEmpty) {
        throw NoSuchElementException("ArrayQueue is empty")
    } else {
        @Suppress("UNCHECKED_CAST")
        poll() as T
    }
