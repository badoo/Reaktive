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

fun <T> AtomicList<T>.remove(element: T): Boolean {
    var removed = false

    update {
        val newList = it - element
        removed = newList.size < it.size
        newList
    }

    return removed
}

operator fun <T> AtomicList<T>.minusAssign(element: T) {
    remove(element)
}

fun <T> AtomicList<T>.clear() {
    update { emptyList() }
}

operator fun <T> AtomicList<T>.get(index: Int): T = value[index]

operator fun <T> AtomicList<T>.set(index: Int, element: T): T =
    getAndUpdate {
        it.replace(index, element)
    }[index]

fun <T> AtomicList<T>.firstOrNull(): T? = value.firstOrNull()

val AtomicReference<out Collection<*>>.isEmpty: Boolean get() = value.isEmpty()

val AtomicReference<out Collection<*>>.isNotEmpty: Boolean get() = value.isNotEmpty()

operator fun <T> AtomicReference<out Iterable<T>>.iterator(): Iterator<T> = value.iterator()