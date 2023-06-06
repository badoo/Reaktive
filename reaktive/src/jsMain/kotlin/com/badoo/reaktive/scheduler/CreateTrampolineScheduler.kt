package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.clock.DefaultClock
import kotlin.time.Duration

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            busySleep(it)
            true
        }
    )

private fun busySleep(duration: Duration) {
    val end = DefaultClock.uptime + duration
    while (DefaultClock.uptime < end) {
        // no-op
    }
}
