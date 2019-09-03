package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.freeze
import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.isFrozen

actual class AtomicReference<T> actual constructor(initialValue: T) {

    private val delegate = FreezableAtomicReference(initialValue)

    actual var value: T
        get() = delegate.value
        set(value) {
            delegate.value = value.freezeIfNeeded()
        }

    actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
        delegate.compareAndSet(expectedValue, newValue.freezeIfNeeded())

    private fun T.freezeIfNeeded(): T {
        if (delegate.isFrozen) {
            freeze()
        }

        return this
    }
}