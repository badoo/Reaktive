package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.atomic.AtomicInt
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

class SchedulerCoroutineDispatcherJvmJsTest {

    private val scheduler = TestScheduler(isManualProcessing = true)
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
}
