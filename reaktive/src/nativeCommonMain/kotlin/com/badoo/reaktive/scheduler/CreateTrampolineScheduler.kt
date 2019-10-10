package com.badoo.reaktive.scheduler

import kotlinx.cinterop.convert
import platform.posix.usleep

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            usleep((it * SECOND_IN_MILLIS).convert())
            true
        }
    )

private const val SECOND_IN_MILLIS = 1_000L