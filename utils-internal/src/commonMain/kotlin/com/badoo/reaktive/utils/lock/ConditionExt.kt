package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi
import com.badoo.reaktive.utils.clock.DefaultClock

@InternalReaktiveApi
inline fun Condition.waitFor(timeoutNanos: Long, predicate: () -> Boolean): Boolean {
    require(timeoutNanos >= 0L) { "Timeout must be not be negative" }

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
