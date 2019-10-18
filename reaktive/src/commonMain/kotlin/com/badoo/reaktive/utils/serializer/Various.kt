package com.badoo.reaktive.utils.serializer

internal expect inline fun <T> serializer(crossinline onValue: (T) -> Boolean): Serializer<T>

internal expect inline fun <T : Any> serializer(
    comparator: Comparator<in T>,
    crossinline onValue: (T) -> Boolean
): Serializer<T>
