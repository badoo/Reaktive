package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
expect class AtomicInt(initialValue: Int = 0) {

    var value: Int

    fun addAndGet(delta: Int): Int

    fun compareAndSet(expectedValue: Int, newValue: Int): Boolean
}
