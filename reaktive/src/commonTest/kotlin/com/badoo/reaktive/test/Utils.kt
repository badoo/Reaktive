package com.badoo.reaktive.test

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler

internal fun mockUncaughtExceptionHandler(): AtomicReference<Throwable?> {
    val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
    reaktiveUncaughtErrorHandler = { caughtException.value = it }

    return caughtException
}
