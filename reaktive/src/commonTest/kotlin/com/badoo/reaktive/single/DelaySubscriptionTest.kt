package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DelaySubscriptionTest :
    SingleToSingleTests by SingleToSingleTestsImpl({ delaySubscription(0L, TestScheduler()) }) {

    private val upstream = TestSingle<Int?>()
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
    fun succeeds_WHEN_upstream_succeeded() {
        val observer = upstream.delaySubscription(delayMillis = 0L, scheduler = scheduler).test()

        upstream.onSuccess(null)

        observer.assertSuccess(null)
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
