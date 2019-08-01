package com.badoo.reaktive.utils

internal fun <T> List<T>.plusSorted(item: T, comparator: Comparator<in T>): List<T> =
    toCollection(ArrayList(size + 1)).apply {
        val index = binarySearch(item, comparator).let { if (it >= 0) it else -it - 1 }
        add(index, item)
    }