package com.badoo.reaktive.scheduler

import com.badoo.reaktive.looperthread.FixedLooperThreadStrategy
import com.badoo.reaktive.utils.processorCount
import kotlin.math.max

actual fun createComputationScheduler(): Scheduler = SchedulerImpl(FixedLooperThreadStrategy(threadCount))

private val threadCount: Int get() = max(2, processorCount())
