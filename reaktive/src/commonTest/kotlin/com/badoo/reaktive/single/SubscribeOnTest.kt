package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SubscribeOnTest : SingleToSingleTests by SingleToSingleTestsImpl({ subscribeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestSingle<Int>()
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
    fun succeeds_synchronously() {
        scheduler.process()
        observer.reset()
        upstream.onSuccess(0)

        observer.assertSuccess(0)
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
