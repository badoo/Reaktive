package com.badoo.reaktive.coroutinesinterop.test

import com.badoo.reaktive.utils.lock.Condition
import com.badoo.reaktive.utils.lock.waitFor
import kotlin.test.fail

internal fun Condition.waitForOrFail(timeoutNanos: Long = 5_000_000_000L, predicate: () -> Boolean) {
    if (!waitFor(timeoutNanos, predicate)) {
        fail("Timeout waiting for condition")
    }
}
