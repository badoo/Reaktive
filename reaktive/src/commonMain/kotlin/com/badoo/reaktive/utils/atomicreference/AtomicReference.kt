package com.badoo.reaktive.utils.atomicreference

internal expect class AtomicReference<T>(
    initialValue: T,
    autoFreeze: Boolean = false
) {

    var value: T

    fun getAndSet(value: T): T

    fun compareAndSet(expectedValue: T, newValue: T): Boolean
}