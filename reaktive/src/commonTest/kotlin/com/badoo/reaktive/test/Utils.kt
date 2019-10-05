package com.badoo.reaktive.test

import com.badoo.reaktive.utils.Condition
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.clock.DefaultClock
import com.badoo.reaktive.utils.reaktiveUncaughtErrorHandler
import kotlin.test.fail

internal fun Condition.waitFor(timeoutNanos: Long, predicate: () -> Boolean): Boolean {
    require(timeoutNanos > 0L) { "Timeout must be a positive value" }

    val endNanos = DefaultClock.uptimeNanos + timeoutNanos
    var remainingNanos = timeoutNanos

    while (true) {
        if (predicate()) {
            return true
        }
        if (remainingNanos <= 0L) {
            return false
        }

        await(remainingNanos)
        remainingNanos = endNanos - DefaultClock.uptimeNanos
    }
}

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