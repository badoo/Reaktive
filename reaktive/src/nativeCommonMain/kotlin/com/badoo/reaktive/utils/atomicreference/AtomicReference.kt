package com.badoo.reaktive.utils.atomicreference

import com.badoo.reaktive.utils.freeze

internal actual class AtomicReference<T> actual constructor(
    initialValue: T,
    private val autoFreeze: Boolean
) {

    private val delegate = kotlin.native.concurrent.AtomicReference(initialValue.freezeIfNeeded())

    actual var value: T
        get() = delegate.value
        set(value) {
            delegate.value = value.freezeIfNeeded()
        }

    actual fun getAndSet(value: T): T {
        value.freezeIfNeeded()
        var v: T
        do {
            v = delegate.value
        } while (!delegate.compareAndSet(v, value))

        return v
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