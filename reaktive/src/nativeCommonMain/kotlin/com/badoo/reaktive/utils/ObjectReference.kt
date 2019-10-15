package com.badoo.reaktive.utils

import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

internal actual class ObjectReference<T> actual constructor(initialValue: T) {

    private val delegate = FreezableAtomicReference(initialValue)

    actual var value: T
        get() = delegate.value
        set(value) {
            delegate.value = value.freezeIfNeeded()
        }

    private fun T.freezeIfNeeded(): T {
        if (delegate.isFrozen) {
            freeze()
        }

        return this
    }
}
