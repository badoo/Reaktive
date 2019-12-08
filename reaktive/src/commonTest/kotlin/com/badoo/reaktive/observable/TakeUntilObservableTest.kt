package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertFalse

class TakeUntilObservableTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ takeUntil(observableOfNever<Nothing>()) }) {

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
    fun unsubscribes_from_upstream_WHEN_other_produced_non_null_value() {
        other.onNext(Unit)

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_other_produced_null_value() {
        other.onNext(null)

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_other_WHEN_other_produced_non_null_value() {
        other.onNext(Unit)

        assertFalse(other.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_other_WHEN_other_produced_null_value() {
        other.onNext(null)

        assertFalse(other.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_other_completed() {
        other.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_other_produced_error() {
        other.onError(Exception())

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun produces_values_in_correct_order_from_upstream() {
        upstream.onNext(0, null, 1, null, 2)

        observer.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun unsubscribes_from_other_WHEN_upstream_completed() {
        upstream.onComplete()

        assertFalse(other.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_other_WHEN_upstream_produced_error() {
        upstream.onError(Exception())

        assertFalse(other.hasSubscribers)
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
