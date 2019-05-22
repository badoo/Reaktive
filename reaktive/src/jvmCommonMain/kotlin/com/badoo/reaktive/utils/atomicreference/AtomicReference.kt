package com.badoo.reaktive.utils.atomicreference

actual class AtomicReference<T> actual constructor(
    initialValue: T,
    autoFreeze: Boolean
) {

    private val delegate = java.util.concurrent.atomic.AtomicReference<T>(initialValue)

    actual var value: T
        get() = delegate.get()
        set(value) {
            delegate.set(value)
        }

    actual fun getAndSet(value: T): T = delegate.getAndSet(value)

    actual fun compareAndSet(expectedValue: T, newValue: T): Boolean = delegate.compareAndSet(expectedValue, newValue)
}