package com.badoo.reaktive.utils.atomic

actual class AtomicBoolean actual constructor(initialValue: Boolean) {

    private val delegate = java.util.concurrent.atomic.AtomicBoolean(initialValue)

    actual var value: Boolean
        get() = delegate.get()
        set(value) {
            delegate.set(value)
        }

    actual fun compareAndSet(expectedValue: Boolean, newValue: Boolean): Boolean =
        delegate.compareAndSet(expectedValue, newValue)
}
