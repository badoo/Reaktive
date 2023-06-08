package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.utils.clock.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class) // UnconfinedTestDispatcher is experimental
class CoroutineContextSchedulerTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val clock = TestClock()
    private val scheduler = CoroutineContextScheduler(context = dispatcher, clock = TestClock())
    private val executor = scheduler.newExecutor()
    private val task = TestTask()

    @Test
    fun executes_task_immediately_WHEN_no_delay() {
        executor.submit(task = task::run)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_task_WHEN_delay_not_reached() {
        executor.submit(delay = 100.milliseconds, task = task::run)
        advanceTimeBy(99L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_task_WHEN_delay_reached() {
        executor.submit(delay = 100.milliseconds, task = task::run)
        advanceTimeBy(100L)

        task.assertSingleRun()
    }

    @Test
    fun executes_task_WHEN_one_task_submitted_and_cancelled_and_another_task_submitted_and_delay_reached() {
        executor.submit(delay = 100.milliseconds, task = task::run)
        executor.cancel()
        executor.submit(delay = 100.milliseconds, task = task::run)
        advanceTimeBy(100L)

        task.assertSingleRun()
    }

    @Test
    fun executes_repeating_task_immediately_WHEN_no_delay() {
        executor.submit(period = 1.seconds, task = task::run)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_delay_not_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(99L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_repeating_task_WHEN_delay_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_period_not_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        advanceTimeBy(999L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_repeating_task_second_time_WHEN_period_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        advanceTimeBy(1000L)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_period_not_reached_second_time() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        advanceTimeBy(999L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_repeating_task_third_time_WHEN_period_reached_second_time() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        advanceTimeBy(1000L)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_task_WHEN_executor_cancelled_and_delay_reached() {
        executor.submit(delay = 100.milliseconds, task = task::run)
        executor.cancel()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_executor_cancelled_and_delay_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        executor.cancel()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_executor_cancelled_and_period_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        executor.cancel()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_executor_cancelled_and_period_reached_second_time() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        executor.cancel()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_task_WHEN_executor_disposed_and_delay_reached() {
        executor.submit(delay = 100.milliseconds, task = task::run)
        executor.dispose()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_executor_disposed_and_delay_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        executor.dispose()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_executor_disposed_and_period_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        executor.dispose()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_executor_disposed_and_period_reached_second_time() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        executor.dispose()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_task_WHEN_scheduler_destroyed_and_delay_reached() {
        executor.submit(delay = 100.milliseconds, task = task::run)
        scheduler.destroy()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_scheduler_destroyed_and_delay_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        scheduler.destroy()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_scheduler_destroyed_and_period_reached() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        scheduler.destroy()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_scheduler_destroyed_and_period_reached_second_time() {
        executor.submit(delay = 100.milliseconds, period = 1.seconds, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        scheduler.destroy()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_dispatch_new_task_WHEN_previous_task_is_pending_and_new_one_submitted() {
        var dispatchCount = 0

        val dispatcher =
            object : CoroutineDispatcher() {
                override fun dispatch(context: CoroutineContext, block: Runnable) {
                    dispatchCount++
                }
            }

        val scheduler = CoroutineContextScheduler(context = dispatcher, clock = TestClock())
        val executor = scheduler.newExecutor()
        executor.submit(task = task::run)
        dispatchCount = 0

        executor.submit(task = task::run)

        assertEquals(0, dispatchCount)
    }

    @Test
    fun executor_isDisposed_returns_false_WHEN_not_disposed() {
        assertFalse(executor.isDisposed)
    }

    @Test
    fun executor_isDisposed_returns_false_WHEN_cancelled() {
        executor.cancel()

        assertFalse(executor.isDisposed)
    }

    @Test
    fun executor_isDisposed_returns_true_WHEN_disposed() {
        executor.dispose()

        assertTrue(executor.isDisposed)
    }

    private fun advanceTimeBy(millis: Long) {
        clock.advanceBy(millis.milliseconds)
        dispatcher.scheduler.advanceTimeBy(millis)
        dispatcher.scheduler.runCurrent()
    }

    private class TestClock : Clock {
        override var uptime: Duration = Duration.ZERO

        fun advanceBy(duration: Duration) {
            uptime += duration
        }
    }

    private class TestTask {
        private var runCount = 0

        fun run() {
            runCount++
        }

        fun assertDidNotRun() {
            assertEquals(0, runCount)
        }

        fun assertSingleRun() {
            assertEquals(1, runCount)
        }

        fun reset() {
            runCount = 0
        }
    }
}
