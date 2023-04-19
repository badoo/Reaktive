package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
expect class AtomicReference<T>(initialValue: T) {

    var value: T

    fun compareAndSet(expectedValue: T, newValue: T): Boolean
}
