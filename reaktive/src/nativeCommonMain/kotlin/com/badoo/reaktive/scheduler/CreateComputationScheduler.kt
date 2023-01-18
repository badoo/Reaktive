package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.FixedLooperThreadStrategy
import kotlinx.cinterop.UnsafeNumber
import platform.posix._SC_NPROCESSORS_ONLN
import platform.posix.sysconf
import kotlin.math.max

actual fun createComputationScheduler(): Scheduler = SchedulerImpl(FixedLooperThreadStrategy(threadCount))

@OptIn(UnsafeNumber::class)
private val threadCount: Int
    get() = max(2, sysconf(_SC_NPROCESSORS_ONLN).toInt())
