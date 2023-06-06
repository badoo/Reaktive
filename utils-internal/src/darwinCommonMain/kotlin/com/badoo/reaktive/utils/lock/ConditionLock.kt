package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi
import platform.Foundation.NSCondition
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSinceNow
import kotlin.system.getTimeNanos
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.DurationUnit

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
        val startTime = getTimeNanos().nanoseconds
        condition.waitUntilDate(NSDate.dateWithTimeIntervalSinceNow(timeout.toDouble(DurationUnit.SECONDS)))

        return startTime + timeout - getTimeNanos().nanoseconds
    }

    actual fun signal() {
        condition.broadcast()
    }
}
