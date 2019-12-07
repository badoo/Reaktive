package com.badoo.reaktive.completable

import com.badoo.reaktive.observable.DelayErrorTests
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.assertNotComplete
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test

class DelayTest :
    CompletableToCompletableTests by CompletableToCompletableTestsImpl({ delay(0L, TestScheduler()) }),
    DelayErrorTests by DelayErrorTests(
        TestCompletable(),
        { delayMillis, scheduler, delayError -> delay(delayMillis, scheduler, delayError).test() }
    ) {

    private val upstream = TestCompletable()
    private val scheduler = TestScheduler()
    private val timer = scheduler.timer
    private val observer = upstream.delay(1000L, scheduler).test()

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
