package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.setValue
import kotlin.native.concurrent.FreezableAtomicReference

internal actual open class PairReference<T, R> actual constructor(firstInitial: T, secondInitial: R) {

    private val _first = FreezableAtomicReference(firstInitial)

    actual var first: T
        get() = _first.value
        set(value) {
            _first.setValue(value)
        }

    private val _second = FreezableAtomicReference(secondInitial)

    actual var second: R
        get() = _second.value
        set(value) {
            _second.setValue(value)
        }
}
