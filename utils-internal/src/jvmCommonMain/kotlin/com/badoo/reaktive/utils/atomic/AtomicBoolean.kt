package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual class AtomicBoolean actual constructor(initialValue: Boolean) : java.util.concurrent.atomic.AtomicBoolean(initialValue) {

    actual var value: Boolean
        get() = super.get()
        set(value) {
            super.set(value)
        }
}
