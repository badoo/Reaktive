package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.CachedLooperThreadStrategy

actual fun createIoScheduler(): Scheduler = SchedulerImpl(CachedLooperThreadStrategy(keepAliveTimeoutMillis = 60000L))
