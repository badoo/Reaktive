package com.badoo.reaktive.test.scheduler

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class TestSchedulerWithAutoProcessingTest {

    private val scheduler = TestScheduler(isManualProcessing = false)
    private val timer get() = scheduler.timer
    private val task = Task()

    @Test
    fun task_called_WHEN_submitted_with_zero_delay() {
        scheduler.newExecutor().submit(delay = 0.milliseconds, task = task.ref)

        task.assertCalled()
    }

    @Test
    fun task_not_called_WHEN_submitted_with_positive_delay() {
        scheduler.newExecutor().submit(delay = 1.milliseconds, task = task.ref)

        task.assertNotCalled()
    }

    @Test
    fun task_not_called_WHEN_submitted_with_positive_delay_and_delay_not_reached() {
        scheduler.newExecutor().submit(delay = 2.milliseconds, task = task.ref)
        timer.advanceBy(1.milliseconds)

        task.assertNotCalled()
    }

    @Test
    fun task_called_WHEN_submitted_with_positive_delay_and_delay_reached() {
        scheduler.newExecutor().submit(delay = 1.milliseconds, task = task.ref)
        timer.advanceBy(1.milliseconds)

        task.assertCalled()
    }

    @Test
    fun task_called_WHEN_submitted_with_positive_delay_and_delay_over_reached() {
        scheduler.newExecutor().submit(delay = 1.milliseconds, task = task.ref)
        timer.advanceBy(2.milliseconds)

        task.assertCalled()
    }

    @Test
    fun task_called_WHEN_submitted_with_zero_delay_and_positive_period() {
        scheduler.newExecutor().submit(delay = 0.milliseconds, period = 1.milliseconds, task = task.ref)

        task.assertCalled()
    }

    @Test
    fun task_not_called_WHEN_submitted_with_zero_delay_and_positive_period_and_period_not_reached() {
        scheduler.newExecutor().submit(delay = 0.milliseconds, period = 2.milliseconds, task = task.ref)
        task.reset()

        timer.advanceBy(1.milliseconds)

        task.assertNotCalled()
    }

    @Test
    fun task_called_WHEN_submitted_with_zero_delay_and_positive_period_and_period_reached() {
        scheduler.newExecutor().submit(delay = 0.milliseconds, period = 1.milliseconds, task = task.ref)
        task.reset()

        timer.advanceBy(1.milliseconds)

        task.assertCalled()
    }

    @Test
    fun task_called_WHEN_submitted_with_delay_and_period_and_delay_reached() {
        scheduler.newExecutor().submit(delay = 1.milliseconds, period = 1.milliseconds, task = task.ref)

        timer.advanceBy(1.milliseconds)

        task.assertCalled()
    }

    @Test
    fun task_not_called_WHEN_submitted_with_delay_and_period_and_period_not_reached() {
        scheduler.newExecutor().submit(delay = 1.milliseconds, period = 2.milliseconds, task = task.ref)
        timer.advanceBy(1.milliseconds)
        task.reset()

        timer.advanceBy(1.milliseconds)

        task.assertNotCalled()
    }

    @Test
    fun task_called_WHEN_submitted_with_delay_and_period_and_period_reached() {
        scheduler.newExecutor().submit(delay = 1.milliseconds, period = 1.milliseconds, task = task.ref)
        timer.advanceBy(1.milliseconds)
        task.reset()

        timer.advanceBy(1.milliseconds)

        task.assertCalled()
    }

    @Test
    fun task_called_multiple_times_WHEN_submitted_with_zero_delay_and_period_and_timer_advanced() {
        scheduler.newExecutor().submit(delay = 0.milliseconds, period = 200.milliseconds, task = task.ref)
        timer.advanceBy(1000.milliseconds)

        task.assertCalled(count = 6)
    }

    @Test
    fun task_called_multiple_times_WHEN_submitted_with_delay_and_period_and_timer_advanced() {
        scheduler.newExecutor().submit(delay = 100.milliseconds, period = 200.milliseconds, task = task.ref)
        timer.advanceBy(1100.milliseconds)

        task.assertCalled(count = 6)
    }

    private class Task {
        var callCount = 0
        val ref: () -> Unit = { callCount++ }

        fun reset() {
            callCount = 0
        }

        fun assertCalled(count: Int = 1) {
            assertEquals(count, callCount)
        }

        fun assertNotCalled() {
            assertEquals(0, callCount)
        }
    }
}
