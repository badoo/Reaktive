package com.badoo.reaktive.utils.serializer

internal inline fun <T> serializer(
    crossinline onValue: (T) -> Boolean,
): Serializer<T> =
    object : DefaultSerializer<T>() {
        override fun onValue(value: T): Boolean =
            onValue.invoke(value)
    }

internal abstract class DefaultSerializer<T> : AbstractSerializer<T>() {
    private var queue: ArrayDeque<T>? = null

    override fun addLast(value: T) {
        val queue = queue ?: ArrayDeque<T>().also { queue = it }
        queue.add(value)
    }

    override fun clearQueue() {
        queue?.clear()
    }

    override fun isEmpty(): Boolean =
        queue?.isEmpty() ?: true

    override fun removeFirst(): T =
        requireNotNull(queue).removeFirst()
}
