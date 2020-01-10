package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscribeOnTest
    : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ subscribeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.subscribeOn(scheduler).test()

    @Test
    fun does_not_subscribe_synchronously() {
        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun subscribes_through_scheduler() {
        scheduler.process()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun succeeds_with_non_null_synchronously() {
        scheduler.process()
        observer.reset()
        upstream.onSuccess(0)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_null_synchronously() {
        scheduler.process()
        observer.reset()
        upstream.onSuccess(null)

        observer.assertSuccess(null)
    }

    @Test
    fun completes_synchronously() {
        scheduler.process()
        observer.reset()
        upstream.onComplete()

        observer.assertComplete()
    }

    @Test
    fun errors_synchronously() {
        scheduler.process()
        val error = Throwable()
        observer.reset()
        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        observer.dispose()

        scheduler.assertAllExecutorsDisposed()
    }
}
