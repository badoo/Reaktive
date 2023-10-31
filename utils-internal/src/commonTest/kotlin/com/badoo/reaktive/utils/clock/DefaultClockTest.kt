package com.badoo.reaktive.utils.clock

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

class DefaultClockTest {

    @Test
    fun uptime_increases() {
        val time1 = DefaultClock.uptime
        busySleep(1.milliseconds)
        val time2 = DefaultClock.uptime

        assertTrue(time2 - time1 >= 1.milliseconds)
    }

    private fun busySleep(duration: Duration) {
        val end = TimeSource.Monotonic.markNow()
        while (end.elapsedNow() < duration) {
            // no-op
        }
    }
}
