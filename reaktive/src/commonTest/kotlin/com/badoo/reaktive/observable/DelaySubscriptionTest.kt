package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.time.Duration.Companion.milliseconds

class DelaySubscriptionTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ delaySubscription(0.milliseconds, TestScheduler()) }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ delaySubscription(0.milliseconds, TestScheduler()) }) {

    private val upstream = TestObservable<Int?>()
    private val scheduler = TestScheduler()

    @Test
    fun does_not_subscribe_to_upstream_WHEN_timeout_not_reached() {
        upstream.delaySubscription(delay = 10.milliseconds, scheduler = scheduler).test()

        scheduler.timer.advanceBy(9L)

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun subscribes_to_upstream_only_once_WHEN_timeout_reached() {
        upstream.delaySubscription(delay = 10.milliseconds, scheduler = scheduler).test()

        scheduler.timer.advanceBy(10L)

        assertEquals(1, upstream.observers.size)
    }

    @Test
    fun emits_all_values_in_the_same_order() {
        val observer = upstream.delaySubscription(delay = 0.milliseconds, scheduler = scheduler).test()

        upstream.onNext(0, null, 1)

        observer.assertValues(0, null, 1)
    }

    @Test
    fun disposes_executor_WHEN_timeout_reached() {
        upstream.delaySubscription(delay = 10.milliseconds, scheduler = scheduler).test()

        scheduler.timer.advanceBy(10L)

        scheduler.assertAllExecutorsDisposed()
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        val observer = upstream.delaySubscription(delay = 10.milliseconds, scheduler = scheduler).test()

        observer.dispose()

        scheduler.assertAllExecutorsDisposed()
    }
}
