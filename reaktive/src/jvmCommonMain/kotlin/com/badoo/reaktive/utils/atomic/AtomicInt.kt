package com.badoo.reaktive.utils.atomic

actual class AtomicInt actual constructor(initialValue: Int) {

    private val delegate = java.util.concurrent.atomic.AtomicInteger(initialValue)

    actual var value: Int
        get() = delegate.get()
        set(value) {
            delegate.set(value)
        }

    actual fun incrementAndGet(delta: Int): Int = delegate.addAndGet(delta)

    actual fun compareAndSet(expectedValue: Int, newValue: Int): Boolean = delegate.compareAndSet(expectedValue, newValue)
}