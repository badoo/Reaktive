package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.clock.DefaultClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
fun CoroutineContext.asScheduler(): Scheduler =
    CoroutineContextScheduler(
        context = this,
        clock = DefaultClock
    )
