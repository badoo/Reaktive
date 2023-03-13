package com.badoo.reaktive.test

import com.badoo.reaktive.utils.lock.Lock
import com.badoo.reaktive.utils.lock.synchronized

internal expect fun doInBackground(block: () -> Unit)

fun <T> doInBackgroundBlocking(timeoutNanos: Long = 5_000_000_000L, block: () -> T): T {
    val lock = Lock()
    val condition = lock.newCondition()
    var isFinished = false
    var result: T? = null

    doInBackground {
        result = block()
        lock.synchronized {
            isFinished = true
            condition.signal()
        }
    }

    lock.synchronized {
        condition.waitForOrFail(timeoutNanos) { isFinished }
    }

    @Suppress("UNCHECKED_CAST")
    return result as T
}
