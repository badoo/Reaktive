package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.FixedLooperThreadStrategy

actual fun createSingleScheduler(): Scheduler = SchedulerImpl(FixedLooperThreadStrategy(threadCount = 1))
