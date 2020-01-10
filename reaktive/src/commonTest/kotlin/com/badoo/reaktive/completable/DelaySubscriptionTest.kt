package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DelaySubscriptionTest :
    CompletableToCompletableTests by CompletableToCompletableTestsImpl({ delaySubscription(0L, TestScheduler()) }) {

    private val upstream = TestCompletable()
    private val scheduler = TestScheduler()

    @Test
    fun does_not_subscribe_to_upstream_WHEN_timeout_not_reached() {
        upstream.delaySubscription(delayMillis = 10L, scheduler = scheduler).test()

        scheduler.timer.advanceBy(9L)

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun subscribes_to_upstream_only_once_WHEN_timeout_reached() {
        upstream.delaySubscription(delayMillis = 10L, scheduler = scheduler).test()

        scheduler.timer.advanceBy(10L)

        assertEquals(1, upstream.observers.size)
    }

    @Test
    fun disposes_executor_WHEN_timeout_reached() {
        upstream.delaySubscription(delayMillis = 10L, scheduler = scheduler).test()

        scheduler.timer.advanceBy(10L)

        scheduler.assertAllExecutorsDisposed()
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        val observer = upstream.delaySubscription(delayMillis = 10L, scheduler = scheduler).test()

        observer.dispose()

        scheduler.assertAllExecutorsDisposed()
    }
}
