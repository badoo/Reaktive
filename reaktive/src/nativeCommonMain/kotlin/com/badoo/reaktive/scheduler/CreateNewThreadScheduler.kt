package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.UnboundLooperThreadStrategy

actual fun createNewThreadScheduler(): Scheduler = SchedulerImpl(UnboundLooperThreadStrategy)
