package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.clock.DefaultClock

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            busySleep(it)
            true
        }
    )

@Suppress("EmptyWhileBlock")
private fun busySleep(millis: Long) {
    val end = DefaultClock.uptimeMillis + millis
    while (DefaultClock.uptimeMillis < end) {
    }
}
