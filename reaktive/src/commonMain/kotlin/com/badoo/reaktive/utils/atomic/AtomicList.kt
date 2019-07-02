package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.insert
import com.badoo.reaktive.utils.replace

typealias AtomicList<T> = AtomicReference<List<T>>

fun <T> atomicList(initialList: List<T> = emptyList()): AtomicList<T> = AtomicList(initialList, true)

fun <T> AtomicList<T>.add(element: T) {
    update { it + element }
}

operator fun <T> AtomicList<T>.plusAssign(element: T) {
    add(element)
}

fun <T> AtomicList<T>.add(index: Int, element: T) {
    update { it.insert(index, element) }
}

fun <T> AtomicList<T>.removeAt(index: Int): T =
    getAndUpdate {
        it.filterIndexed { i, _ -> i != index }
    }[index]

fun <T> AtomicList<T>.clear() {
    update { emptyList() }
}

operator fun <T> AtomicList<T>.get(index: Int): T = value[index]

operator fun <T> AtomicList<T>.set(index: Int, element: T): T =
    getAndUpdate {
        it.replace(index, element)
    }[index]

operator fun <T> AtomicReference<out Iterable<T>>.iterator(): Iterator<T> = value.iterator()