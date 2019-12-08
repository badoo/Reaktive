package com.badoo.reaktive.maybe

import com.badoo.reaktive.observable.DelayErrorTests
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.assertComplete
import com.badoo.reaktive.test.maybe.assertNotComplete
import com.badoo.reaktive.test.maybe.assertNotSuccess
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test

class DelayTest :
    MaybeToMaybeTests by MaybeToMaybeTestsImpl({ delay(0L, TestScheduler()) }),
    DelayErrorTests by DelayErrorTests<TestMaybe<Int>>(
        TestMaybe(),
        { delayMillis, scheduler, delayError -> delay(delayMillis, scheduler, delayError).test() }
    ) {

    private val upstream = TestMaybe<Int?>()
    private val scheduler = TestScheduler()
    private val timer = scheduler.timer
    private val observer = upstream.delay(1000L, scheduler).test()

    @Test
    fun does_not_succeed_IF_timeout_not_reached() {
        upstream.onSuccess(0)
        timer.advanceBy(999L)

        observer.assertNotSuccess()
    }

    @Test
    fun succeeds_with_non_null_WHEN_timeout_reached() {
        upstream.onSuccess(0)

        timer.advanceBy(1000L)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_with_null_WHEN_timeout_reached() {
        upstream.onSuccess(null)

        timer.advanceBy(1000L)

        observer.assertSuccess(null)
    }

    @Test
    fun does_not_complete_synchronously() {
        upstream.onComplete()

        observer.assertNotComplete()
    }

    @Test
    fun does_not_complete_IF_timeout_not_reached() {
        upstream.onComplete()
        timer.advanceBy(999L)

        observer.assertNotComplete()
    }

    @Test
    fun completes_WHEN_timeout_reached() {
        upstream.onComplete()
        timer.advanceBy(1000L)

        observer.assertComplete()
    }

    @Test
    fun not_delayed_error_goes_before_pending_success() {
        upstream.onSuccess(0)

        upstream.onError(Throwable())

        observer.assertError()
    }

    @Test
    fun not_delayed_error_cancels_pending_success() {
        upstream.onSuccess(0)

        upstream.onError(Throwable())

        timer.advanceBy(1000L)
    }

    @Test
    fun not_delayed_error_goes_before_pending_complete() {
        upstream.onComplete()

        upstream.onError(Throwable())

        observer.assertError()
    }

    @Test
    fun not_delayed_error_cancels_pending_complete() {
        upstream.onComplete()

        upstream.onError(Throwable())

        timer.advanceBy(1000L)
    }
}
