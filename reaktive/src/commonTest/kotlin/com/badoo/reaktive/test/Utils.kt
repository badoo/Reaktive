package com.badoo.reaktive.test

import com.badoo.reaktive.utils.Condition
import com.badoo.reaktive.utils.uptimeMillis
import kotlin.test.fail

internal inline fun Condition.waitFor(timeoutMillis: Long, predicate: () -> Boolean): Boolean {
    require(timeoutMillis > 0L) { "Timeout must be a positive value" }

    val endMillis = uptimeMillis + timeoutMillis
    var remainingMillis = timeoutMillis

    while (true) {
        if (predicate()) {
            return true
        }
        if (remainingMillis <= 0L) {
            return false
        }

        await(remainingMillis)
        remainingMillis = endMillis - uptimeMillis
    }
}

internal inline fun Condition.waitForOrFail(timeoutMillis: Long, predicate: () -> Boolean) {
    if (!waitFor(timeoutMillis, predicate)) {
        fail("Timeout waiting for condition")
    }
}