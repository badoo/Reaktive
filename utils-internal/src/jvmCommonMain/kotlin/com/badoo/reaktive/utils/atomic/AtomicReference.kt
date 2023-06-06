package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual class AtomicReference<T> actual constructor(initialValue: T) : java.util.concurrent.atomic.AtomicReference<T>(initialValue) {

    actual var value: T
        get() = super.get()
        set(value) {
            super.set(value)
        }
}
