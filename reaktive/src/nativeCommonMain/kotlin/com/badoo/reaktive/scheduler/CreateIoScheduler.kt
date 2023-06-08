package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.CachedLooperThreadStrategy
import kotlin.time.Duration.Companion.seconds

actual fun createIoScheduler(): Scheduler =
    SchedulerImpl(CachedLooperThreadStrategy(keepAliveTimeout = 60.seconds))
