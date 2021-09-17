package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.MICROS_IN_MILLI
import com.badoo.reaktive.utils.usleep

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            usleep((it * MICROS_IN_MILLI).toUInt())
            true
        }
    )
