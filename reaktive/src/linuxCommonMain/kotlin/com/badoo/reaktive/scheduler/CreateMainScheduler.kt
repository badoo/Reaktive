package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.FixedLooperThreadStrategy

actual fun createMainScheduler(): Scheduler = SchedulerImpl(FixedLooperThreadStrategy(1))
