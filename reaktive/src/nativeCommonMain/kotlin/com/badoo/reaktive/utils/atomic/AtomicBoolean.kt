package com.badoo.reaktive.utils.atomic

actual class AtomicBoolean actual constructor(initialValue: Boolean) {

    private val delegate = kotlin.native.concurrent.AtomicInt(initialValue.intValue)

    actual var value: Boolean
        get() = delegate.value != 0
        set(value) {
            delegate.value = value.intValue
        }

    actual fun compareAndSet(expectedValue: Boolean, newValue: Boolean): Boolean =
        delegate.compareAndSet(expectedValue.intValue, newValue.intValue)

    private companion object {
        private val Boolean.intValue: Int get() = if (this) 1 else 0
    }
}