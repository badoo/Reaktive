package com.badoo.reaktive.utils.atomic

actual class AtomicLong actual constructor(initialValue: Long) {

    actual var value: Long = initialValue

    actual fun incrementAndGet(delta: Long): Long {
        value += delta

        return value
    }

    actual fun compareAndSet(expectedValue: Long, newValue: Long): Boolean =
        if (value == expectedValue) {
            value = newValue
            true
        } else {
            false
        }
}