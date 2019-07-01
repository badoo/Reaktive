package com.badoo.reaktive.utils.atomic

actual class AtomicReference<T> actual constructor(
    initialValue: T,
    autoFreeze: Boolean
) {

    actual var value: T = initialValue

    actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
        if (value == expectedValue) {
            value = newValue
            true
        } else {
            false
        }
}