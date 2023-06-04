package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.clock.DefaultClock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class SchedulerCoroutineDispatcherTest {

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

    @OptIn(ExperimentalCoroutinesApi::class) // runTest is experimental
    @Test
    fun executes_with_delay() = runTest {
        val scheduler = TestScheduler(isManualProcessing = false)
        val dispatcher = SchedulerCoroutineDispatcher(scheduler = scheduler)
        val startTime = DefaultClock.uptime
        val endTime = AtomicReference(Duration.ZERO)

        launch(dispatcher) {
            delay(500.milliseconds)
            endTime.value = DefaultClock.uptime
        }

        withContext(Dispatchers.Default) {
            while (endTime.value == Duration.ZERO) {
                yield()
            }
        }

        assertTrue(endTime.value - startTime >= 500.milliseconds)
    }

    private class TestRunnable : Runnable {
        private var runCount = 0

        override fun run() {
            runCount++
        }

        fun assertNotExecuted() {
            assertEquals(0, runCount)
        }

        fun assertExecutedOnce() {
            assertEquals(1, runCount)
        }
    }
}
