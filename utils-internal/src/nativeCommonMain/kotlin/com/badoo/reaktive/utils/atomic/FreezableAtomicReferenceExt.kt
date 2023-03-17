package com.badoo.reaktive.utils.atomic

import kotlin.native.concurrent.FreezableAtomicReference
import kotlin.native.concurrent.freeze
import kotlin.native.concurrent.isFrozen

/**
 * Freezes the value if the [FreezableAtomicReference] is frozen and then sets the value
 */
fun <T> FreezableAtomicReference<T>.setValue(value: T) {
    if (isFrozen) {
        value.freeze()
    }

    this.value = value
}
