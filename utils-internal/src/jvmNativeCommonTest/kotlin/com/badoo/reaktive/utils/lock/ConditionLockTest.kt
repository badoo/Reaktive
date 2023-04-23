package com.badoo.reaktive.utils.lock

import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.test.doInBackground
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ConditionLockTest {

    @Test
    fun awaitNanos_returns_non_positive_result_WHEN_timeout_reached() {
        val lock = ConditionLock()

        val result = lock.synchronized { lock.await(1.milliseconds) }

        assertFalse(result.isPositive())
    }

    @Test
    fun awaitNanos_without_timeout_returns_positive_WHEN_signalled() {
        val lock = ConditionLock()
        val isReady = AtomicBoolean()

        doInBackground {
            while (!isReady.value) {
                // no-op
            }

            lock.synchronized { lock.signal() }
        }

        val result =
            lock.synchronized {
                isReady.value = true
                lock.await()
            }

        assertTrue(result.isPositive())
    }

    @Test
    fun awaitNanos_with_timeout_returns_positive_result_less_than_timeout_WHEN_signalled_before_timeout() {
        val lock = ConditionLock()
        val timeout = 5.seconds
        val isReady = AtomicBoolean()

        doInBackground {
            while (!isReady.value) {
                // no-op
            }

            lock.synchronized { lock.signal() }
        }

        val result =
            lock.synchronized {
                isReady.value = true
                lock.await(timeout)
            }

        assertTrue(result.isPositive() && (result < timeout))
    }
}
