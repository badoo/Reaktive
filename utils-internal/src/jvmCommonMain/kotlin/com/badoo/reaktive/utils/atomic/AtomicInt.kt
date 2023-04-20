package com.badoo.reaktive.utils.atomic

import com.badoo.reaktive.utils.InternalReaktiveApi

@InternalReaktiveApi
actual class AtomicInt actual constructor(initialValue: Int) : java.util.concurrent.atomic.AtomicInteger(initialValue) {

    actual var value: Int
        get() = super.get()
        set(value) {
            super.set(value)
        }

    // See KT-16087
    override fun toByte(): Byte = value.toByte()
    override fun toChar(): Char = value.toChar()
    override fun toShort(): Short = value.toShort()
}
