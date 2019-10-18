package com.badoo.reaktive.utils.serializer

internal actual inline fun <T> serializer(crossinline onValue: (T) -> Boolean): Serializer<T> =
    object : SerializerImpl<T>() {
        override fun onValue(value: T): Boolean = onValue.invoke(value)
    }

internal actual inline fun <T : Any> serializer(
    comparator: Comparator<in T>,
    crossinline onValue: (T) -> Boolean
): Serializer<T> =
    object : SerializerImpl<T>(comparator) {
        override fun onValue(value: T): Boolean = onValue.invoke(value)
    }
