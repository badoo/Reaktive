package com.badoo.reaktive.utils.test

import com.badoo.reaktive.utils.lock.ConditionLock
import com.badoo.reaktive.utils.lock.synchronized
import com.badoo.reaktive.utils.lock.waitFor
import kotlin.test.fail
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

expect fun doInBackground(block: () -> Unit)

fun doInBackgroundBlocking(timeout: Duration = 5.seconds, block: () -> Unit) {
    val lock = ConditionLock()
    var isFinished = false

    doInBackground {
        block()
        lock.synchronized {
            isFinished = true
            lock.signal()
        }
    }

    lock.synchronized {
        if (!lock.waitFor(timeout) { isFinished }) {
            fail("Timeout waiting for condition")
        }
    }
}
