package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertNotSuccess
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertTrue

class ObserveOnTest : SingleToSingleTests by SingleToSingleTestsImpl({ observeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestSingle<Int>()
    private val observer = upstream.observeOn(scheduler).test()

    @Test
    fun subscribes_synchronously() {
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_no_succeed_synchronously() {
        upstream.onSuccess(0)

        observer.assertNotSuccess()
    }

    @Test
    fun succeeds_through_scheduler() {
        upstream.onSuccess(0)
        scheduler.process()

        observer.assertSuccess(0)
    }

    @Test
    fun does_not_error_synchronously() {
        upstream.onError(Throwable())

        observer.assertNotError()
    }

    @Test
    fun errors_through_scheduler() {
        val error = Throwable()
        upstream.onError(error)
        scheduler.process()

        observer.assertError(error)
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        observer.dispose()

        scheduler.assertAllExecutorsDisposed()
    }
}
