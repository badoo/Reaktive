package com.badoo.reaktive.utils.serializer

/**
 * See [Serializer]
 */
internal inline fun <T> serializer(crossinline onValue: (T) -> Boolean): Serializer<T> =
    object : Serializer<T>() {
        override fun onValue(value: T): Boolean = onValue(value)
    }