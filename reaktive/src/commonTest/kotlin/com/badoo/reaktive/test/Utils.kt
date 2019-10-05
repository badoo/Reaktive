package com.badoo.reaktive.test

import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.lock.Condition
import com.badoo.reaktive.utils.lock.waitFor
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import kotlin.test.fail

internal fun Condition.waitForOrFail(timeoutNanos: Long, predicate: () -> Boolean) {
    if (!waitFor(timeoutNanos, predicate)) {
        fail("Timeout waiting for condition")
    }
}

internal fun mockUncaughtExceptionHandler(): AtomicReference<Throwable?> {
    val caughtException: AtomicReference<Throwable?> = AtomicReference(null)
    reaktiveUncaughtErrorHandler = { caughtException.value = it }

    return caughtException
}