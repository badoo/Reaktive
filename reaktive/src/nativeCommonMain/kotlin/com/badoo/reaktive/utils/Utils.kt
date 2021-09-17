package com.badoo.reaktive.utils

// Workaround for KT-43309
internal expect fun processorCount(): Int

// Workaround for KT-43309
internal expect fun usleep(millis: UInt)

internal fun <T> List<T>.plusSorted(item: T, comparator: Comparator<in T>): List<T> =
    toCollection(ArrayList(size + 1)).apply {
        val index =
            binarySearch(item, comparator).let {
                // binarySearch returns "-insertion point - 1" if item is not found
                if (it >= 0) it else -it - 1
            }

        add(index, item)
    }
