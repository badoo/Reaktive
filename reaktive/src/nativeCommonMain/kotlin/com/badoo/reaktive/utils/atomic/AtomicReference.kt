package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.freeze

actual class AtomicReference<T> actual constructor(
    initialValue: T,
    private val autoFreeze: Boolean
) {

    private val delegate = kotlin.native.concurrent.AtomicReference(initialValue.freezeIfNeeded())

    actual var value: T
        get() = delegate.value
        set(value) {
            delegate.value = value.freezeIfNeeded()
        }

    actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
        delegate.compareAndSet(expectedValue, newValue.freezeIfNeeded())

    private fun T.freezeIfNeeded(): T {
        if (autoFreeze) {
            freeze()
        }

        return this
    }
}