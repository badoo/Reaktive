package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.isComplete
import com.badoo.reaktive.test.maybe.isError
import com.badoo.reaktive.test.maybe.isSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.maybe.value
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ObserveOnTest
    : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ observeOn(TestScheduler()) }) {

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

        assertFalse(observer.isSuccess)
    }

    @Test
    fun succeeds_with_non_null_through_scheduler() {
        upstream.onSuccess(0)
        scheduler.process()

        assertEquals(0, observer.value)
    }

    @Test
    fun succeeds_with_null_through_scheduler() {
        upstream.onSuccess(null)
        scheduler.process()

        assertEquals(null, observer.value)
    }

    @Test
    fun does_no_complete_synchronously() {
        upstream.onComplete()

        assertFalse(observer.isComplete)
    }

    @Test
    fun completes_through_scheduler() {
        upstream.onComplete()
        scheduler.process()

        assertTrue(observer.isComplete)
    }

    @Test
    fun does_not_error_synchronously() {
        upstream.onError(Throwable())

        assertFalse(observer.isError)
    }

    @Test
    fun errors_through_scheduler() {
        val error = Throwable()
        upstream.onError(error)
        scheduler.process()

        assertTrue(observer.isError(error))
    }

    @Test
    fun disposes_executor_WHEN_disposed() {
        observer.dispose()

        assertTrue(scheduler.executors.all(TestScheduler.Executor::isDisposed))
    }
}