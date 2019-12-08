package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertValue
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.scheduler.TestScheduler
import kotlin.test.Test

class DebounceTest
    : ObservableToObservableTests by ObservableToObservableTestsImpl({ debounce(0L, TestScheduler()) }) {

    private val upstream = TestObservable<Int?>()
    private val scheduler = TestScheduler()
    private val observer = upstream.debounce(100L, scheduler).test()

    @Test
    fun does_not_emit_WHEN_timeout_not_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(99)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_is_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)

        observer.assertValue(1)
    }

    @Test
    fun does_not_emit_WHEN_timeout_since_last_item_not_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(50L)
        upstream.onNext(2)
        scheduler.timer.advanceBy(99L)

        observer.assertNoValues()
    }

    @Test
    fun emits_WHEN_timeout_since_last_item_is_reached() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(50L)
        upstream.onNext(2)
        scheduler.timer.advanceBy(100L)

        observer.assertValue(2)
    }

    @Test
    fun emits_last_unprocessed_item_WHEN_completed() {
        upstream.onNext(1)
        upstream.onNext(2)
        upstream.onComplete()

        observer.assertValue(2)
    }

    @Test
    fun does_not_emit_last_item_WHEN_already_emitted_and_completed() {
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)
        observer.reset()
        upstream.onComplete()

        observer.assertNoValues()
    }

    @Test
    fun produces_values_in_correct_order() {
        upstream.onNext(0)
        scheduler.timer.advanceBy(100L)

        upstream.onNext(1) // Should be ignored
        scheduler.timer.advanceBy(99L)
        upstream.onNext(2)
        scheduler.timer.advanceBy(100L)

        upstream.onNext(null) // Should be ignored
        upstream.onNext(null)
        scheduler.timer.advanceBy(100L)
        upstream.onNext(5)
        scheduler.timer.advanceBy(100L)

        upstream.onNext(6) // Should be ignored
        scheduler.timer.advanceBy(99L)
        upstream.onNext(null)
        scheduler.timer.advanceBy(100L)

        upstream.onNext(8)
        upstream.onComplete()

        observer.assertValues(0, 2, null, 5, null, 8)
    }

    @Test
    fun produces_error_WHEN_source_produced_error() {
        val error = Throwable()

        upstream.onNext(0)
        upstream.onError(error)

        observer.assertNoValues()
        observer.assertError(error)
    }

    @Test
    fun produces_values_and_error_WHEN_source_produced_error_after_timeout() {
        val error = Throwable()

        upstream.onNext(null)
        scheduler.timer.advanceBy(100L)
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)
        upstream.onError(error)

        observer.assertValues(null, 1)
        observer.assertError(error)
    }

    @Test
    fun does_not_produce_more_values_WHEN_disposed() {
        upstream.onNext(0)
        scheduler.timer.advanceBy(100L)
        observer.reset()

        observer.dispose()
        upstream.onNext(1)
        scheduler.timer.advanceBy(100L)

        observer.assertNoValues()
    }

}
