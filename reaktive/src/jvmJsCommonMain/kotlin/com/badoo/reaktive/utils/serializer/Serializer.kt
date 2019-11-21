package com.badoo.reaktive.utils.serializer

import com.badoo.reaktive.utils.queue.ArrayQueue
import com.badoo.reaktive.utils.queue.PriorityQueue

internal actual inline fun <T> serializer(crossinline onValue: (T) -> Boolean): Serializer<T> =
    object : SerializerImpl<T>(ArrayQueue<T>()) {
        override fun onValue(value: T): Boolean = onValue.invoke(value)
    }

internal actual inline fun <T : Any> serializer(
    comparator: Comparator<in T>,
    crossinline onValue: (T) -> Boolean
): Serializer<T> =
    object : SerializerImpl<T>(PriorityQueue<T>(comparator)) {
        override fun onValue(value: T): Boolean = onValue.invoke(value)
    }
