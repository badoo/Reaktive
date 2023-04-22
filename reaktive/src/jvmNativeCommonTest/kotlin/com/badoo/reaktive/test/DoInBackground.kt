package com.badoo.reaktive.test

import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.waitForOrFail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal expect fun doInBackground(block: () -> Unit)

fun <T> doInBackgroundBlocking(timeout: Duration = 5.seconds, block: () -> T): T {
    val lock = ConditionLock()
    var isFinished = false
    var result: T? = null

    doInBackground {
        result = block()
        lock.synchronized {
            isFinished = true
            lock.signal()
        }
    }

    lock.synchronized {
        lock.waitForOrFail(timeout) { isFinished }
    }

    @Suppress("UNCHECKED_CAST")
    return result as T
}
