package com.badoo.reaktive.utils.atomic

actual class AtomicLong actual constructor(initialValue: Long) {

    private val delegate = java.util.concurrent.atomic.AtomicLong(initialValue)

    actual var value: Long
        get() = delegate.get()
        set(value) {
            delegate.set(value)
        }

    actual fun addAndGet(delta: Long): Long = delegate.addAndGet(delta)

    actual fun compareAndSet(expectedValue: Long, newValue: Long): Boolean =
        delegate.compareAndSet(expectedValue, newValue)
}
