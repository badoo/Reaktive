package com.badoo.reaktive.utils.atomic

actual class AtomicInt actual constructor(initialValue: Int) {

    actual var value: Int = initialValue

    actual fun addAndGet(delta: Int): Int {
        value += delta

        return value
    }

    actual fun compareAndSet(expectedValue: Int, newValue: Int): Boolean =
        if (value == expectedValue) {
            value = newValue
            true
        } else {
            false
        }
}