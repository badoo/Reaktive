package com.badoo.reaktive.single

import com.badoo.reaktive.observable.DelayErrorTests
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.scheduler.TestScheduler
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.assertNotSuccess
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

class DelayTest :
    SingleToSingleTests by SingleToSingleTestsImpl({ delay(0L, TestScheduler()) }),
    DelayErrorTests by DelayErrorTests<TestSingle<Int>>(
        TestSingle(),
        { delayMillis, scheduler, delayError -> delay(delayMillis, scheduler, delayError).test() }
    ) {

    private val upstream = TestSingle<Int?>()
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
}
