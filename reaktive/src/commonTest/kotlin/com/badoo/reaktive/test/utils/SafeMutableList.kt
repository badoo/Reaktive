package com.badoo.reaktive.test.utils

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.update

class SafeMutableList<T> {

    private val ref = AtomicReference<List<T>>(emptyList(), true)
    val items: List<T> get() = ref.value

    operator fun get(index: Int): T = ref.value[index]

    fun add(value: T) {
        ref.update { it + value }
    }

    operator fun plusAssign(value: T) {
        add(value)
    }
}