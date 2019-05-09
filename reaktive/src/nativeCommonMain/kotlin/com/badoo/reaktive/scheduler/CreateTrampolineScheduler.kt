package com.badoo.reaktive.scheduler

import kotlinx.cinterop.convert
import platform.posix.usleep

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            usleep((it * 1000L).convert())
            true
        }
    )