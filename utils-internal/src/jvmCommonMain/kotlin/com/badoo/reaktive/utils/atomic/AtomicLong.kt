package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual class AtomicLong actual constructor(initialValue: Long) : java.util.concurrent.atomic.AtomicLong(initialValue) {

    actual var value: Long
        get() = super.get()
        set(value) {
            super.set(value)
        }

    // See KT-16087
    override fun toByte(): Byte = value.toByte()
    override fun toShort(): Short = value.toShort()
}
