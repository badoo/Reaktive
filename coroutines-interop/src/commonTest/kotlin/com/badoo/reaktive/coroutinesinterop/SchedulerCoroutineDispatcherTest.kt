package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

@UseExperimental(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
class SchedulerCoroutineDispatcherTest {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val timer = scheduler.timer
    private val dispatcher = SchedulerCoroutineDispatcher(scheduler = scheduler)

    @Test
    fun does_not_execute_callback_synchronously() {
        val runnable = TestRunnable()
        dispatcher.dispatch(EmptyCoroutineContext, runnable)

        runnable.assertNotExecuted()
    }

    @Test
    fun executes_callback_via_scheduler() {
        val runnable = TestRunnable()
        dispatcher.dispatch(EmptyCoroutineContext, runnable)
        scheduler.process()

        runnable.assertExecutedOnce()
    }

    @Test
    fun does_not_execute_delayed_callback_WHEN_timeout_not_reached() {
        val runnable = TestRunnable()
        dispatcher.invokeOnTimeout(1000L, runnable)
        timer.advanceBy(999L)
        scheduler.process()

        runnable.assertNotExecuted()
    }

    @Test
    fun executes_delayed_callback_WHEN_timeout_reached() {
        val runnable = TestRunnable()
        dispatcher.invokeOnTimeout(1000L, runnable)
        timer.advanceBy(1000L)
        scheduler.process()

        runnable.assertExecutedOnce()
    }

    @Test
    fun does_not_execute_delayed_callback_WHEN_task_disposed_and_timeout_reached() {
        val runnable = TestRunnable()
        val handle = dispatcher.invokeOnTimeout(1000L, runnable)
        handle.dispose()
        timer.advanceBy(1000L)
        scheduler.process()

        runnable.assertNotExecuted()
    }

    @Test
    fun does_not_resume_delayed_continuation_WHEN_timeout_not_reached() {
        val continuation = TestCancellableContinuation()
        dispatcher.scheduleResumeAfterDelay(1000L, continuation)
        timer.advanceBy(999L)
        scheduler.process()

        continuation.assertResumeUndispatchedNotInvoked()
    }

    @Test
    fun resumes_delayed_continuation_WHEN_timeout_reached() {
        val continuation = TestCancellableContinuation()
        dispatcher.scheduleResumeAfterDelay(1000L, continuation)
        timer.advanceBy(1000L)
        scheduler.process()

        continuation.assertResumeUndispatchedInvokedOnce()
    }

    @Test
    fun does_not_resume_delayed_continuation_WHEN_cancelled_and_timeout_reached() {
        val continuation = TestCancellableContinuation()
        dispatcher.scheduleResumeAfterDelay(1000L, continuation)
        continuation.cancel()
        timer.advanceBy(1000L)
        scheduler.process()

        continuation.assertResumeUndispatchedNotInvoked()
    }

    private class TestRunnable : Runnable {
        private val runCount = AtomicInt()

        override fun run() {
            runCount.addAndGet(1)
        }

        fun assertNotExecuted() {
            assertEquals(0, runCount.value)
        }

        fun assertExecutedOnce() {
            assertEquals(1, runCount.value)
        }
    }

    private class TestCancellableContinuation : CancellableContinuation<Unit> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override val isActive: Boolean = true
        override val isCancelled: Boolean = false
        override val isCompleted: Boolean = false
        private val completionHandler = AtomicReference<CompletionHandler?>(null)
        private val resumeUndispatchedCount = AtomicInt()

        override fun cancel(cause: Throwable?): Boolean {
            completionHandler.value?.invoke(null)

            return true
        }

        @InternalCoroutinesApi
        override fun completeResume(token: Any) {
        }

        @InternalCoroutinesApi
        override fun initCancellability() {
        }

        override fun invokeOnCancellation(handler: CompletionHandler) {
            completionHandler.value = handler
        }

        @ExperimentalCoroutinesApi
        override fun resume(value: Unit, onCancellation: (cause: Throwable) -> Unit) {
        }

        override fun resumeWith(result: Result<Unit>) {
        }

        @InternalCoroutinesApi
        override fun tryResume(value: Unit, idempotent: Any?): Any? = null

        @InternalCoroutinesApi
        override fun tryResumeWithException(exception: Throwable): Any? = null

        @ExperimentalCoroutinesApi
        override fun CoroutineDispatcher.resumeUndispatched(value: Unit) {
            resumeUndispatchedCount.addAndGet(1)
        }

        @ExperimentalCoroutinesApi
        override fun CoroutineDispatcher.resumeUndispatchedWithException(exception: Throwable) {
        }

        fun assertResumeUndispatchedNotInvoked() {
            assertEquals(0, resumeUndispatchedCount.value)
        }

        fun assertResumeUndispatchedInvokedOnce() {
            assertEquals(1, resumeUndispatchedCount.value)
        }
    }
}
