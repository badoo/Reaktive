package com.badoo.reaktive.utils.threadlocal

class ThreadLocalStorage<T : Any>(initialValue: T? = null) {

    private val key = ThreadLocalState.allocateKey()

    var value: T?
        @Suppress("UNCHECKED_CAST")
        get() = ThreadLocalState[key] as T?
        set(value) {
            ThreadLocalState[key] = value
        }

    init {
        value = initialValue
    }

    fun clear() {
        value = null
    }
}