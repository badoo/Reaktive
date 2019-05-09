package com.badoo.reaktive.utils.serializer

/**
 * See [Serializer]
 */
internal inline fun <T> serializer(
    comparator: Comparator<in T>? = null,
    crossinline onValue: (T) -> Boolean
): Serializer<T> =
    object : Serializer<T>(comparator) {
        override fun onValue(value: T): Boolean = onValue(value)
    }