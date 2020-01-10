package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class IntervalTest {

    private val scheduler = TestScheduler()
    private val timer = scheduler.timer
    private val upstream = observableInterval(50L, 100L, scheduler)
    private val observer = upstream.test()

    @Test
    fun calls_onSubscribe_only_once_WHEN_subscribed() {
        observer.assertSubscribed()
    }

    @Test
    fun does_not_call_onError_ON_subscribe() {
        observer.assertNotError()
    }

    @Test
    fun acquires_executor_WHEN_subscribed() {
        assertFalse(scheduler.executors.all(Scheduler.Executor::isDisposed))
    }

    @Test
    fun does_not_emit_values_IF_start_delay_not_reached() {
        timer.advanceBy(49L)
        observer.assertNoValues()
    }

    @Test
    fun does_not_complete_IF_start_delay_not_reached() {
        timer.advanceBy(49L)
        observer.assertNotComplete()
    }

    @Test
    fun does_not_call_onError_IF_start_delay_not_reached() {
        timer.advanceBy(49L)
        observer.assertNotError()
    }

    @Test
    fun emit_single_value_WHEN_start_delay_reached() {
        timer.advanceBy(50L)
        observer.assertValue(0L)
    }

    @Test
    fun does_not_complete_WHEN_start_delay_reached() {
        timer.advanceBy(50L)
        observer.assertNotComplete()
    }

    @Test
    fun does_not_call_onError_WHEN_start_delay_reached() {
        timer.advanceBy(50L)
        observer.assertNotError()
    }

    @Test
    fun emit_values_periodically_AFTER_task_executed_few_times() {
        timer.advanceBy(50L)
        observer.reset()

        timer.advanceBy(500L)
        observer.assertValues(1L, 2L, 3L, 4L, 5L)
    }

    @Test
    fun does_not_call_onError_AFTER_task_executed_few_times() {
        timer.advanceBy(50L)
        observer.reset()

        timer.advanceBy(500L)
        observer.assertNotError()
    }

    @Test
    fun does_not_complete_AFTER_task_executed_few_times() {
        timer.advanceBy(50L)
        observer.reset()

        timer.advanceBy(500L)
        observer.assertNotComplete()
    }

    @Test
    fun onNext_ignored_AFTER_dispose() {
        observer.dispose()
        timer.advanceBy(100L)
        observer.assertNoValues()
    }

    @Test
    fun onComplete_ignored_AFTER_dispose() {
        observer.dispose()
        timer.advanceBy(100L)
        observer.assertNotComplete()
    }

    @Test
    fun disposes_AFTER_dispose() {
        observer.dispose()
        observer.assertDisposed()
    }

    @Test
    fun disposes_executor_AFTER_dispose() {
        observer.dispose()
        scheduler.assertAllExecutorsDisposed()
    }

    @Test
    fun does_not_call_onError_ON_dispose() {
        observer.dispose()
        observer.assertNotError()
    }

}
