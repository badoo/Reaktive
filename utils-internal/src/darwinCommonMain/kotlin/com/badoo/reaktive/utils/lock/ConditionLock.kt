package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi
import platform.Foundation.NSCondition
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.TimeSource

@InternalReaktiveApi
actual open class ConditionLock {

    private val condition = NSCondition()

    actual fun lock() {
        condition.lock()
    }

    actual fun unlock() {
        condition.unlock()
    }

    actual fun await(timeout: Duration): Duration =
        if (timeout.isInfinite()) {
            condition.wait()
            Duration.INFINITE
        } else {
            awaitTimed(timeout = timeout.coerceAtLeast(Duration.ZERO))
        }

    private fun awaitTimed(timeout: Duration): Duration {
        val startTime = TimeSource.Monotonic.markNow()
        condition.waitUntilDate(NSDate.dateWithTimeIntervalSinceNow(timeout.toDouble(DurationUnit.SECONDS)))

        return startTime + timeout - TimeSource.Monotonic.markNow()
    }

    actual fun signal() {
        condition.broadcast()
    }
}
