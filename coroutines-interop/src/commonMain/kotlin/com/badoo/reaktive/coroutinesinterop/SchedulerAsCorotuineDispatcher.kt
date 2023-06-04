package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.scheduler.Scheduler
import kotlinx.coroutines.CoroutineDispatcher

fun Scheduler.asCoroutineDispatcher(): CoroutineDispatcher = SchedulerCoroutineDispatcher(this)
