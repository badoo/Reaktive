package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.UnboundLooperThreadStrategy

actual fun createIoScheduler(): Scheduler = SchedulerImpl(UnboundLooperThreadStrategy())