package com.badoo.reaktive.scheduler

import kotlin.time.Duration.Companion.milliseconds

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(
        sleep = {
            try {
                Thread.sleep(it.inWholeMilliseconds, (it.inWholeNanoseconds % 1.milliseconds.inWholeNanoseconds).toInt())
                true
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                false
            }
        }
    )
