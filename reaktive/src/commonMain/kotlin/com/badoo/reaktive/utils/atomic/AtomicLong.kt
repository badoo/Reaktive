package com.badoo.reaktive.utils.atomic

expect class AtomicLong(initialValue: Long = 0L) {

    var value: Long

    fun incrementAndGet(delta: Long): Long

    fun compareAndSet(expectedValue: Long, newValue: Long): Boolean
}