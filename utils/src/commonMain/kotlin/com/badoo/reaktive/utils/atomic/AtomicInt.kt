package com.badoo.reaktive.utils.atomic

expect class AtomicInt(initialValue: Int = 0) {

    var value: Int

    fun addAndGet(delta: Int): Int

    fun compareAndSet(expectedValue: Int, newValue: Int): Boolean
}