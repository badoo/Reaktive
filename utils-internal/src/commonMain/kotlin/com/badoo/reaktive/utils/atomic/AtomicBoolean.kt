package com.badoo.reaktive.utils.atomic

expect class AtomicBoolean(initialValue: Boolean = false) {

    var value: Boolean

    fun compareAndSet(expectedValue: Boolean, newValue: Boolean): Boolean
}
