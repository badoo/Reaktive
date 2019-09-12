package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertTrue

class TakeUntilObservableTest :
    ObservableToObservableTests by ObservableToObservableTests<Unit>({ takeUntil(observableOfNever<Nothing>()) }) {

    private val upstream = TestObservable<Int?>()
    private val other = TestObservable<Unit?>()
    private val observer = upstream.takeUntil(other).test()

    @Test
    fun completes_WHEN_other_produced_non_null_value() {
        other.onNext(Unit)

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_other_produced_null_value() {
        other.onNext(null)

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_other_produced_value_after_upstream_values() {
        upstream.onNext(0, 1, 2)

        other.onNext(Unit)

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_other_completed() {
        other.onComplete()

        observer.assertComplete()
    }

    @Test
    fun completes_WHEN_other_completed_after_upstream_values() {
        upstream.onNext(0, 1, 2)
        other.onComplete()

        observer.assertComplete()
    }

    @Test
    fun produces_same_error_WHEN_other_produced_error() {
        val error = Exception()

        other.onError(error)

        observer.assertError(error)
    }

    @Test
    fun produces_same_error_WHEN_other_produced_error_after_upstream_values() {
        val error = Exception()

        upstream.onNext(0, 1, 2)
        other.onError(error)

        observer.assertError(error)
    }

    @Test
    fun disposes_upstream_disposable_WHEN_other_produced_non_null_value() {
        other.onNext(Unit)

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun disposes_upstream_disposable_WHEN_other_produced_null_value() {
        other.onNext(null)

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun disposes_other_disposable_WHEN_other_produced_non_null_value() {
        other.onNext(Unit)

        assertTrue(other.isDisposed)
    }

    @Test
    fun disposes_other_disposable_WHEN_other_produced_null_value() {
        other.onNext(null)

        assertTrue(other.isDisposed)
    }

    @Test
    fun disposes_upstream_disposable_WHEN_other_completed() {
        other.onComplete()

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun disposes_upstream_disposable_WHEN_other_produced_error() {
        other.onError(Exception())

        assertTrue(upstream.isDisposed)
    }

    @Test
    fun produces_values_in_correct_order_from_upstream() {
        upstream.onNext(0, null, 1, null, 2)

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun disposes_other_disposable_WHEN_upstream_completed() {
        upstream.onComplete()

        assertTrue(other.isDisposed)
    }

    @Test
    fun disposes_other_disposable_WHEN_upstream_produced_error() {
        upstream.onError(Exception())

        assertTrue(other.isDisposed)
    }

    @Test
    fun does_not_complete_second_time_WHEN_other_produced_value_after_upstream_completed() {
        upstream.onComplete()
        other.onNext(Unit)
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_complete_second_time_WHEN_other_completed_after_upstream_completed() {
        upstream.onComplete()
        other.onComplete()
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_error_WHEN_other_produced_error_after_upstream_completed() {
        upstream.onComplete()
        other.onError(Exception())
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_complete_WHEN_other_produced_value_after_upstream_produced_error() {
        upstream.onError(Exception())
        other.onNext(Unit)
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_complete_second_WHEN_other_completed_after_upstream_produced_error() {
        upstream.onError(Exception())
        other.onComplete()
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_error_second_time_WHEN_other_produced_error_after_upstream_produced_error() {
        upstream.onError(Exception())
        other.onError(Exception())
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_values_WHEN_upstream_produced_value_after_other_produced_value() {
        other.onNext(Unit)
        upstream.onNext(1)
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_complete_second_time_WHEN_upstream_completed_after_other_produced_value() {
        other.onNext(Unit)
        upstream.onComplete()
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_error_WHEN_upstream_produced_error_after_other_produced_value() {
        other.onNext(Unit)
        upstream.onError(Exception())
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_values_WHEN_upstream_produced_value_after_other_completed() {
        other.onComplete()
        upstream.onNext(1)
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_complete_second_time_WHEN_upstream_completed_after_other_completed() {
        other.onComplete()
        upstream.onComplete()
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_error_WHEN_upstream_produced_error_after_other_completed() {
        other.onComplete()
        upstream.onError(Exception())
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_values_WHEN_upstream_produced_value_after_other_produced_error() {
        other.onError(Exception())
        upstream.onNext(1)
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_complete_second_time_WHEN_upstream_completed_after_other_produced_error() {
        other.onError(Exception())
        upstream.onComplete()
        // Verified automatically by TestObserver
    }

    @Test
    fun does_not_produce_error_second_time_WHEN_upstream_produced_error_after_other_produced_error() {
        other.onError(Exception())
        upstream.onError(Exception())
        // Verified automatically by TestObserver
    }
}