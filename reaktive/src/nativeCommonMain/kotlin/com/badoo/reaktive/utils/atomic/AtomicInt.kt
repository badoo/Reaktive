package com.badoo.reaktive.utils.atomic

actual class AtomicInt actual constructor(initialValue: Int) {

    private val delegate = kotlin.native.concurrent.AtomicInt(initialValue)

    actual var value: Int
        get() = delegate.value
        set(value) {
            delegate.value = value
        }

    actual fun incrementAndGet(delta: Int): Int = delegate.addAndGet(delta)

    actual fun compareAndSet(expectedValue: Int, newValue: Int): Boolean = delegate.compareAndSet(expectedValue, newValue)
}