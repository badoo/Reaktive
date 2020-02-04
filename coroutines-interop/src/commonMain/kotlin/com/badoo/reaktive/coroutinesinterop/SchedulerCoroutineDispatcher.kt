package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.scheduler.submit
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
internal class SchedulerCoroutineDispatcher(
    private val scheduler: Scheduler
) : CoroutineDispatcher(), Delay {

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        scheduler.submit(task = block::run)
    }

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val disposable =
            scheduler.submit(delayMillis = timeMillis) {
                continuation.run { resumeUndispatched(Unit) }
            }

        continuation.invokeOnCancellation {
            disposable.dispose()
        }
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable): DisposableHandle {
        val disposable = scheduler.submit(delayMillis = timeMillis, task = block::run)

        return DisposableHandle(disposable::dispose)
    }
}
