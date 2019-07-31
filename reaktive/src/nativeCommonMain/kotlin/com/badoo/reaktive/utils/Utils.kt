package com.badoo.reaktive.utils

// TODO: Optimise later, we need PriorityQueue
internal fun <T> List<T>.plusSorted(item: T, comparator: Comparator<in T>): List<T> =
    toCollection(ArrayList(size + 1)).apply {
        add(item)
        sortWith(comparator)
    }
