package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.submit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

internal class SchedulerCoroutineDispatcher(
    private val scheduler: Scheduler
) : CoroutineDispatcher() {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        scheduler.submit(task = block::run)
    }
}
