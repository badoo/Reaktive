package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.MICROS_IN_MILLI
import kotlinx.cinterop.convert
import platform.posix.usleep

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            usleep((it * MICROS_IN_MILLI).convert())
            true
        }
    )
    