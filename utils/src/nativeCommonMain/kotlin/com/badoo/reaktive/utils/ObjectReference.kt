package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.setValue
import kotlin.native.concurrent.FreezableAtomicReference

/**
 * A simple mutable reference holder for cases when atomic semantics are not required.
 * In JVM and JS it's just a variable.
 * In Native it's backed by FreezableAtomicReference so the reference can be updated from different threads.
 * Useful when concurrent access is already somehow synchronized and all you need is mutability.
 */
actual open class ObjectReference<T> actual constructor(initialValue: T) {

    private val delegate = FreezableAtomicReference(initialValue)

    actual var value: T
        get() = delegate.value
        set(value) {
            delegate.setValue(value)
        }
}
