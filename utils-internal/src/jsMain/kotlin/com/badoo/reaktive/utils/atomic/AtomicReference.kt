package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual class AtomicReference<T> actual constructor(initialValue: T) {

    actual var value: T = initialValue

    actual fun getAndSet(newValue: T): T =
        value.also { value = newValue }

    actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
        if (value == expectedValue) {
            value = newValue
            true
        } else {
            false
        }
}
