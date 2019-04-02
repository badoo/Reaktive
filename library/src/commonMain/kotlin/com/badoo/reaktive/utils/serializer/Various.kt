package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.queue.Queue
import com.badoo.reaktive.utils.queue.ArrayQueue

/**
 * See [Serializer]
 */
internal inline fun <T> serializer(queue: Queue<T> = ArrayQueue(), crossinline onValue: (T) -> Boolean): Serializer<T> =
    object : Serializer<T>(queue) {
        override fun onValue(value: T): Boolean = onValue.invoke(value)
    }