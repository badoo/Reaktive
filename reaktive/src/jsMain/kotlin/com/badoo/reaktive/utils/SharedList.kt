package com.badoo.reaktive.utils

internal actual class SharedList<T> actual constructor(
    initialCapacity: Int
) : ArrayList<T>(initialCapacity), MutableList<T>