package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
expect class AtomicLong(initialValue: Long = 0L) {

    var value: Long

    fun addAndGet(delta: Long): Long

    fun compareAndSet(expectedValue: Long, newValue: Long): Boolean
}
