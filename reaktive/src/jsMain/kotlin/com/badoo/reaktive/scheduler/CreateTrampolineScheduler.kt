package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.uptimeMillis

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            busySleep(it)
            true
        }
    )

private fun busySleep(millis: Long) {
    val end = uptimeMillis + millis
    while (uptimeMillis < end) {
    }
}