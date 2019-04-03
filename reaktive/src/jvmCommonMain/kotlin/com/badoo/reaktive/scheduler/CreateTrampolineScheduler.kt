package com.badoo.reaktive.scheduler

import com.badoo.reaktive.utils.uptimeMillis

actual fun createTrampolineScheduler(): Scheduler =
    TrampolineScheduler(getUptimeMillis = ::uptimeMillis)