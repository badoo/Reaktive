package com.badoo.reaktive.utils.atomic

actual class AtomicBoolean actual constructor(initialValue: Boolean) {

    actual var value: Boolean = initialValue

    actual fun compareAndSet(expectedValue: Boolean, newValue: Boolean): Boolean =
        if (value == expectedValue) {
            value = newValue
            true
        } else {
            false
        }
}