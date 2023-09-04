package com.badoo.reaktive.scheduler

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.convert
import platform.posix.usleep

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            @OptIn(ExperimentalForeignApi::class)
            usleep(it.inWholeMicroseconds.convert())
            true
        }
    )
