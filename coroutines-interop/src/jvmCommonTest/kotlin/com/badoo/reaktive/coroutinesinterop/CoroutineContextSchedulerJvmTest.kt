package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.utils.NANOS_IN_MILLI
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicLong
import com.badoo.reaktive.utils.clock.Clock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineContextSchedulerJvmTest {

    private val dispatcher = TestCoroutineDispatcher()
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
        executor.submit(delayMillis = 100L, task = task::run)
        advanceTimeBy(99L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_task_WHEN_delay_reached() {
        executor.submit(delayMillis = 100L, task = task::run)
        advanceTimeBy(100L)

        task.assertSingleRun()
    }

    @Test
    fun executes_repeating_task_immediately_WHEN_no_startDelay() {
        executor.submitRepeating(periodMillis = 1000L, task = task::run)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_startDelay_not_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(99L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_repeating_task_WHEN_startDelay_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_period_not_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        advanceTimeBy(999L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_repeating_task_second_time_WHEN_period_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        advanceTimeBy(1000L)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_period_not_reached_second_time() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        advanceTimeBy(999L)

        task.assertDidNotRun()
    }

    @Test
    fun executes_repeating_task_third_time_WHEN_period_reached_second_time() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        advanceTimeBy(1000L)

        task.assertSingleRun()
    }

    @Test
    fun does_not_execute_task_WHEN_executor_cancelled_and_delay_reached() {
        executor.submit(delayMillis = 100L, task = task::run)
        executor.cancel()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_executor_cancelled_and_startDelay_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        executor.cancel()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_executor_cancelled_and_period_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        executor.cancel()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_executor_cancelled_and_period_reached_second_time() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        executor.cancel()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_task_WHEN_executor_disposed_and_delay_reached() {
        executor.submit(delayMillis = 100L, task = task::run)
        executor.dispose()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_executor_disposed_and_startDelay_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        executor.dispose()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_executor_disposed_and_period_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        executor.dispose()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_executor_disposed_and_period_reached_second_time() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        executor.dispose()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_task_WHEN_scheduler_destroyed_and_delay_reached() {
        executor.submit(delayMillis = 100L, task = task::run)
        scheduler.destroy()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_WHEN_scheduler_destroyed_and_startDelay_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        scheduler.destroy()
        advanceTimeBy(100L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_second_time_WHEN_scheduler_destroyed_and_period_reached() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        task.reset()
        scheduler.destroy()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    @Test
    fun does_not_execute_repeating_task_third_time_WHEN_scheduler_destroyed_and_period_reached_second_time() {
        executor.submitRepeating(startDelayMillis = 100L, periodMillis = 1000L, task = task::run)
        advanceTimeBy(100L)
        advanceTimeBy(1000L)
        task.reset()
        scheduler.destroy()
        advanceTimeBy(1000L)

        task.assertDidNotRun()
    }

    private fun advanceTimeBy(millis: Long) {
        clock.advanceBy(millis)
        dispatcher.advanceTimeBy(millis)
    }

    private class TestClock : Clock {
        private val _uptimeMillis = AtomicLong()
        override val uptimeMillis: Long get() = _uptimeMillis.value
        override val uptimeNanos: Long get() = uptimeMillis * NANOS_IN_MILLI

        fun advanceBy(millis: Long) {
            _uptimeMillis.addAndGet(millis)
        }
    }

    private class TestTask {
        private val runCount = AtomicInt()

        fun run() {
            runCount.addAndGet(1)
        }

        fun assertDidNotRun() {
            assertEquals(0, runCount.value)
        }

        fun assertSingleRun() {
            assertEquals(1, runCount.value)
        }

        fun reset() {
            runCount.value = 0
        }
    }
}
