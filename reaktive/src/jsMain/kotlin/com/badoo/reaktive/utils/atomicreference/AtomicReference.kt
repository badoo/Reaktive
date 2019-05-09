package com.badoo.reaktive.utils.atomicreference

internal actual class AtomicReference<T> actual constructor(
    initialValue: T,
    autoFreeze: Boolean
) {

    actual var value: T = initialValue

    actual fun getAndSet(value: T): T {
        val oldValue = this.value
        this.value = value

        return oldValue
    }

    actual fun compareAndSet(expectedValue: T, newValue: T): Boolean =
        if (value == expectedValue) {
            value = newValue
            true
        } else {
            false
        }
}