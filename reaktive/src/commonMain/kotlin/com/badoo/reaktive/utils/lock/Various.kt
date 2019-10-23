package com.badoo.reaktive.utils.lock

internal inline fun <T> withLockAndCondition(block: (Lock, Condition) -> T): T =
    Lock().use { lock ->
        lock.newCondition().use { condition ->
            block(lock, condition)
        }
    }
