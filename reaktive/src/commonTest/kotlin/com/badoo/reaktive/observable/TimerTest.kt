package com.badoo.reaktive.observable

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimerTest {

    private val scheduler = TestScheduler()
    private val timer = scheduler.timer
    private val upstream = observableTimer(1000L, scheduler)
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
    fun does_not_emit_values_IF_timeout_not_reached() {
        timer.advanceBy(999L)
        observer.assertNoValues()
    }

    @Test
    fun does_not_complete_IF_timeout_not_reached() {
        timer.advanceBy(999L)
        observer.assertNotComplete()
    }

    @Test
    fun does_not_call_onError_IF_timeout_not_reached() {
        timer.advanceBy(999L)
        observer.assertNotError()
    }

    @Test
    fun emit_single_value_WHEN_timeout_reached() {
        timer.advanceBy(1000L)
        observer.assertValue(1000L)
    }

    @Test
    fun completes_WHEN_timeout_reached() {
        timer.advanceBy(1000L)
        observer.assertComplete()
    }

    @Test
    fun does_not_call_onError_WHEN_timeout_reached() {
        timer.advanceBy(1000L)
        observer.assertNotError()
    }

    @Test
    fun disposes_WHEN_timeout_reached() {
        timer.advanceBy(1000L)
        observer.assertDisposed()
    }

    @Test
    fun disposes_executor_WHEN_timeout_reached() {
        timer.advanceBy(1000L)
        scheduler.assertAllExecutorsDisposed()
    }

    @Test
    fun onNext_ignored_AFTER_dispose() {
        observer.dispose()
        timer.advanceBy(1000L)
        observer.assertNoValues()
    }

    @Test
    fun onComplete_ignored_AFTER_dispose() {
        observer.dispose()
        timer.advanceBy(1000L)
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
