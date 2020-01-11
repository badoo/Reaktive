package com.badoo.reaktive.maybe

import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.assertSubscribed
import com.badoo.reaktive.test.maybe.assertNotComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TimerTest {

    private val scheduler = TestScheduler()
    private val timer = scheduler.timer
    private val upstream = maybeTimer(1000L, scheduler)
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
    fun does_not_succeed_IF_timeout_not_reached() {
        timer.advanceBy(999L)
        observer.assertNotSuccess()
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
    fun succeed_WHEN_timeout_reached() {
        timer.advanceBy(1000L)
        observer.assertSuccess(1000L)
    }

    @Test
    fun does_not_complete_WHEN_timeout_reached() {
        timer.advanceBy(1000L)
        observer.assertNotComplete()
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
    fun onSuccess_ignored_AFTER_dispose() {
        observer.dispose()
        timer.advanceBy(1000L)
        observer.assertNotSuccess()
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
