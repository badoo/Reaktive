package com.badoo.reaktive.utils.atomic

expect class AtomicReference<T>(initialValue: T) {

    var value: T

    fun compareAndSet(expectedValue: T, newValue: T): Boolean
}
