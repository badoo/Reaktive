package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.clock.DefaultClock
import kotlin.coroutines.CoroutineContext

fun CoroutineContext.asScheduler(): Scheduler =
    CoroutineContextScheduler(
        context = this,
        clock = DefaultClock
    )
