package com.badoo.reaktive.utils

internal actual class SharedList<T> actual constructor(
    initialCapacity: Int
) : java.util.ArrayList<T>(initialCapacity), MutableList<T>