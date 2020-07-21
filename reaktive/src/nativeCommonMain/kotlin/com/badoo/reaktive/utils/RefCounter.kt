package com.badoo.reaktive.utils

import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.updateAndGet

internal class RefCounter(
    private val destroy: () -> Unit
) {

    private val count = AtomicInt(1)

    fun retain(): Boolean =
        count.updateAndGet { if (it > 0) it + 1 else 0 } > 0

    fun release() {
        val newCount = count.updateAndGet { if (it > 0) it - 1 else throw IllegalStateException("RefCounter is already destroyed") }
        if (newCount == 0) {
            destroy()
        }
    }
}
