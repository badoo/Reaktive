package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual class AtomicReference<T> actual constructor(initialValue: T) {

    private val delegate = kotlin.concurrent.AtomicReference(initialValue)

    actual var value: T
        get() = delegate.value
        set(value) {
            delegate.value = value
        }

    actual fun getAndSet(newValue: T): T =
        getAndChange { newValue }

    actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
        delegate.compareAndSet(expectedValue, newValue)
}
