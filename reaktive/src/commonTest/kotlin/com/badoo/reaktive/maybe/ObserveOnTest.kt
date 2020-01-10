package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.assertNotError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertNotComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.scheduler.assertAllExecutorsDisposed
import kotlin.test.Test
import kotlin.test.assertTrue

class ObserveOnTest
    : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ observeOn(TestScheduler()) }) {

    private val scheduler = TestScheduler(isManualProcessing = true)
    private val upstream = TestMaybe<Int?>()
    private val observer = upstream.observeOn(scheduler).test()

    @Test
    fun subscribes_synchronously() {
        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_succeed_synchronously() {
        upstream.onSuccess(0)

        observer.assertNotSuccess()
    }

    @Test
    fun succeeds_with_non_null_through_scheduler() {
        upstream.onSuccess(0)
        scheduler.process()

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_null_through_scheduler() {
        upstream.onSuccess(null)
        scheduler.process()

        observer.assertSuccess(null)
    }

    @Test
    fun does_no_complete_synchronously() {
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun completes_through_scheduler() {
        upstream.onComplete()
        scheduler.process()

        observer.assertComplete()
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
