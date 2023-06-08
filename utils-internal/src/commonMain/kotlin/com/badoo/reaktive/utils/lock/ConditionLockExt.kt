package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.InternalReaktiveApi
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalContracts::class)
@InternalReaktiveApi
inline fun <T> ConditionLock.synchronized(block: () -> T): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    lock()
    try {
        return block()
    } finally {
        unlock()
    }
}

@InternalReaktiveApi
inline fun ConditionLock.waitFor(timeout: Duration, predicate: () -> Boolean): Boolean {
    require(!timeout.isNegative()) { "Timeout must not be negative" }

    var remainingTimeout = timeout

    while (true) {
        if (predicate()) {
            return true
        }
        if (!remainingTimeout.isPositive()) {
            return false
        }

        remainingTimeout = await(timeout = timeout)
    }
}

@InternalReaktiveApi
fun ConditionLock.waitForOrFail(timeout: Duration = 5.seconds, predicate: () -> Boolean) {
    if (!waitFor(timeout, predicate)) {
        error("Timeout waiting for condition")
    }
}
