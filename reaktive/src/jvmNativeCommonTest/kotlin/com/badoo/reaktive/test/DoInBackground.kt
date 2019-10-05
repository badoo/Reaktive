package com.badoo.reaktive.test

import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.lock.synchronized

internal expect fun doInBackground(block: () -> Unit)

fun doInBackgroundBlocking(timeoutNanos: Long = 5_000_000_000L, block: () -> Unit) {
    val lock = Lock()
    val condition = lock.newCondition()
    val isFinished = AtomicBoolean(false)

    doInBackground {
        block()
        lock.synchronized {
            isFinished.value = true
            condition.signal()
        }
    }

    lock.synchronized {
        condition.waitForOrFail(timeoutNanos, isFinished::value)
    }
}