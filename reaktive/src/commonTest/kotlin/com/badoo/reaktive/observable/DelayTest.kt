package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test

class DelayTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ delay(0L, TestScheduler()) }),
    DelayErrorTests by DelayErrorTests<TestObservable<Int>>(
        TestObservable(),
        { delayMillis, scheduler, delayError -> delay(delayMillis, scheduler, delayError).test() }
    ) {

    private val upstream = TestObservable<Int?>()
    private val scheduler = TestScheduler()
    private val timer = scheduler.timer
    private val observer = upstream.delay(1000L, scheduler).test()

    @Test
    fun does_not_emit_values_IF_timeout_not_reached() {
        upstream.onNext(0)
        timer.advanceBy(999L)

        observer.assertNoValues()
    }

    @Test
    fun emits_value_WHEN_timeout_reached() {
        upstream.onNext(0)

        timer.advanceBy(1000L)

        observer.assertValue(0)
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
    fun emits_series_of_values_and_completes_with_delay() {
        upstream.onNext(null)
        timer.advanceBy(100L)
        upstream.onNext(1)
        timer.advanceBy(200L)
        upstream.onNext(2)
        timer.advanceBy(300L)
        upstream.onComplete()

        observer.reset()
        timer.advanceBy(400L)
        observer.assertValue(null)

        observer.reset()
        timer.advanceBy(100L)
        observer.assertValue(1)

        observer.reset()
        timer.advanceBy(200L)
        observer.assertValue(2)

        observer.reset()
        timer.advanceBy(300L)
        observer.assertComplete()
    }

    @Test
    fun not_delayed_error_goes_before_pending_events() {
        upstream.onNext(0)
        upstream.onComplete()

        upstream.onError(Throwable())

        observer.assertError()
    }

    @Test
    fun not_delayed_error_cancels_pending_events() {
        upstream.onNext(0)
        upstream.onComplete()

        upstream.onError(Throwable())

        timer.advanceBy(1000L)
    }
}
