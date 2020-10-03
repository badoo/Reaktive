package com.badoo.reaktive.observable

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.*
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WindowSizedTest {

    private val upstream = TestObservable<Int>()

    private fun window(count: Long = 1, skip: Long = count) = upstream.window(count = count, skip = skip).testWindows().test()

    private fun <T> Observable<Observable<T>>.testWindows(): Observable<TestObservableObserver<T>> = observableUnsafe { observer ->
        subscribe(
            object : ObservableObserver<Observable<T>>, CompletableCallbacks by observer, Observer by observer {
                override fun onNext(value: Observable<T>) {
                    observer.onNext(value.test())
                }
            }
        )
    }

    @Test
    fun first_window_receives_values_WHEN_windows_is_gapless_and_upstream_produced_values() {
        val observer = window(count = 3)

        upstream.onNext(1, 2, 3, 4, 5)

        val window = observer.values.first()
        window.assertValues(1, 2, 3)
    }

    @Test
    fun second_window_receives_values_WHEN_windows_is_gapless_and_upstream_produced_values() {
        val observer = window(count = 3)

        upstream.onNext(1, 2, 3, 4, 5)

        val window = observer.values[1]
        window.assertValues(4, 5)
    }

    @Test
    fun first_window_receives_values_WHEN_windows_is_overlapping_and_upstream_produced_values() {
        val observer = window(count = 3)

        upstream.onNext(1, 2, 3, 4, 5)

        val window = observer.values.first()
        window.assertValues(1, 2, 3)
    }

    @Test
    fun second_window_receives_values_WHEN_windows_is_overlapping_and_upstream_produced_values() {
        val observer = window(count = 3, skip = 2)

        upstream.onNext(1, 2, 3, 4, 5)

        val window = observer.values[1]
        window.assertValues(3, 4, 5)
    }

    @Test
    fun third_window_receives_value_WHEN_windows_is_overlapping_and_upstream_produced_values() {
        val observer = window(count = 3, skip = 2)

        upstream.onNext(1, 2, 3, 4, 5)

        val window = observer.values[2]
        window.assertValues(5)
    }

    @Test
    fun first_window_receives_values_WHEN_windows_is_non_overlapping_and_upstream_produced_values() {
        val observer = window(count = 2, skip = 3)

        upstream.onNext(1, 2, 3, 4, 5)

        val window = observer.values.first()
        window.assertValues(1, 2)
    }

    @Test
    fun second_window_receives_values_WHEN_windows_is_non_overlapping_and_upstream_produced_values() {
        val observer = window(count = 2, skip = 3)

        upstream.onNext(1, 2, 3, 4, 5)

        val window = observer.values[1]
        window.assertValues(4, 5)
    }

    @Test
    fun second_window_receives_values_WHEN_windows_is_gapless_and_upstream_produced_values_and_downstream_disposed() {
        val observer = window(count = 3, skip = 3)

        upstream.onNext(1, 2, 3, 4)
        val windowObserver = observer.values[1]
        observer.dispose()
        upstream.onNext(5, 6)

        windowObserver.assertValues(4, 5, 6)
    }

    @Test
    fun second_window_receives_values_WHEN_windows_is_overlapping_and_upstream_produced_values_and_downstream_disposed() {
        val observer = window(count = 3, skip = 2)

        upstream.onNext(1, 2, 3)
        val windowObserver = observer.values.last()
        observer.dispose()
        upstream.onNext(4)

        windowObserver.assertValues(3, 4)
    }

    @Test
    fun second_window_receives_values_WHEN_windows_id_non_overlapping_and_upstream_produced_values_and_downstream_disposed() {
        val observer = window(count = 2, skip = 3)

        upstream.onNext(1, 2, 3, 4)
        val windowObserver = observer.values.last()
        observer.dispose()
        upstream.onNext(5)

        windowObserver.assertValues(4, 5)
    }

    @Test
    fun disposed_WHEN_windows_is_gapless_and_downstream_disposed() {
        val observer = upstream.window(count = 1).test()

        observer.dispose()

        assertTrue(observer.isDisposed)
    }

    @Test
    fun disposed_WHEN_windows_is_overlapping_and_downstream_disposed() {
        val observer = upstream.window(count = 2, skip = 1).test()

        observer.dispose()

        assertTrue(observer.isDisposed)
    }

    @Test
    fun disposed_WHEN_windows_is_non_overlapping_and_downstream_disposed() {
        val observer = upstream.window(count = 1, skip = 2).test()

        observer.dispose()

        assertTrue(observer.isDisposed)
    }

    @Test
    fun first_window_produces_error_WHEN_windows_is_gapless_and_upstream_produces_values_and_window_subscribed_second_time() {
        val observer = upstream.window(count = 1).test()

        upstream.onNext(0)
        observer.values.first().test()
        val windowObserver = observer.values.first().test()

        windowObserver.assertError()
    }

    @Test
    fun first_window_produces_error_WHEN_windows_is_overlapping_and_upstream_produces_values_and_window_subscribed_second_time() {
        val observer = upstream.window(count = 2, skip = 1).test()

        upstream.onNext(0)
        observer.values.first().test()
        val windowObserver = observer.values.first().test()

        windowObserver.assertError()
    }

    @Test
    fun first_window_produces_error_WHEN_windows_is_non_overlapping_and_upstream_produces_values_and_window_subscribed_second_time() {
        val observer = upstream.window(count = 1, skip = 2).test()

        upstream.onNext(0)
        observer.values.first().test()
        val windowObserver = observer.values.first().test()

        windowObserver.assertError()
    }

    @Test
    fun produces_error_WHEN_windows_is_gapless_and_upstream_produces_error() {
        val observer = window(count = 2)
        val error = Error()

        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_windows_is_overlapping_and_upstream_produces_error() {
        val observer = window(count = 2, skip = 1)
        val error = Error()

        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_windows_is_non_overlapping_and_upstream_produces_error() {
        val observer = window(count = 1, skip = 2)
        val error = Error()

        upstream.onError(error)

        observer.assertError(error)
    }

    @Test
    fun first_window_produces_error_WHEN_windows_is_gapless_and_upstream_produces_values_and_error() {
        val observer = window(count = 2, skip = 2)
        val error = Throwable()

        upstream.onNext(0)
        upstream.onError(error)

        val window = observer.values.first()

        window.assertError(error)
    }

    @Test
    fun first_window_produces_error_WHEN_windows_is_overlapping_and_upstream_produces_values_and_error() {
        val observer = window(count = 4, skip = 2)
        val error = Throwable()

        upstream.onNext(1, 2, 3)
        upstream.onError(error)

        val window = observer.values.first()

        window.assertError(error)
    }

    @Test
    fun second_window_produces_error_WHEN_windows_is_overlapping_and_upstream_produces_values_and_error() {
        val observer = window(count = 4, skip = 2)
        val error = Throwable()

        upstream.onNext(1, 2, 3)
        upstream.onError(error)

        val window = observer.values[1]

        window.assertError(error)
    }

    @Test
    fun first_window_produces_error_WHEN_windows_is_non_overlapping_and_upstream_produces_values_and_error() {
        val observer = window(count = 2, skip = 3)
        val error = Throwable()

        upstream.onNext(1)
        upstream.onError(error)

        val window = observer.values.first()

        window.assertError(error)
    }

    @Test
    fun first_window_completes_WHEN_windows_is_gapless_and_upstream_produces_values_and_completes() {
        val observer = window(count = 2, skip = 2)

        upstream.onNext(1)
        upstream.onComplete()

        val window = observer.values.first()

        window.assertComplete()
    }

    @Test
    fun first_window_completes_WHEN_windows_is_overlapping_and_upstream_produces_values_and_completes() {
        val observer = window(count = 4, skip = 2)

        upstream.onNext(1, 2, 3)
        upstream.onComplete()

        val window = observer.values.first()

        window.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_windows_is_overlapping_and_upstream_produces_values_and_completes() {
        val observer = window(count = 4, skip = 2)

        upstream.onNext(1, 2, 3)
        upstream.onComplete()

        val window = observer.values[1]

        window.assertComplete()
    }

    @Test
    fun first_window_completes_WHEN_windows_is_non_overlapping_and_upstream_produces_values_and_completes() {
        val observer = window(count = 2, skip = 3)

        upstream.onNext(1)
        upstream.onComplete()

        val window = observer.values.first()

        window.assertComplete()
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_gapless_and_upstream_produced_value_and_downstream_disposed_and_window_disposed() {
        val observer = window(count = 2)

        upstream.onNext(1)
        observer.dispose()
        observer.values[0].dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_overlapping_and_upstream_produced_value_and_downstream_disposed_and_window_disposed() {
        val observer = window(count = 3, skip = 2)

        upstream.onNext(1)
        observer.dispose()
        observer.values[0].dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_non_overlapping_and_upstream_produced_value_and_downstream_disposed_and_window_disposed() {
        val observer = window(count = 2, skip = 3)

        upstream.onNext(1)
        observer.dispose()
        observer.values[0].dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_gapless_and_downstream_disposed() {
        val observer = window()

        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_overlapping_and_downstream_disposed() {
        val observer = window(count = 3, skip = 2)

        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_non_overlapping_and_downstream_disposed() {
        val observer = window(count = 2, skip = 3)

        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_gapless_and_upstream_produces_value_and_downstream_not_subscribed_to_window_and_downstream_disposed() {
        val observer = upstream.window(count = 2).test()

        upstream.onNext(1)
        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_windows_is_gapless_upstream_produces_value_and_downstream_subscribed_to_window_and_downstream_disposed() {
        val observer = window(count = 2)

        upstream.onNext(0)
        observer.dispose()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_overlapping_and_upstream_produces_value_and_downstream_not_subscribed_to_window_and_downstream_disposed() {
        val observer = upstream.window(count = 2, skip = 1).test()

        upstream.onNext(1)
        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_windows_is_overlapping_upstream_produces_value_and_downstream_subscribed_to_window_and_downstream_disposed() {
        val observer = window(count = 2, skip = 1)

        upstream.onNext(0)
        observer.dispose()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribe_from_upstream_WHEN_windows_is_non_overlapping_and_upstream_produces_value_and_downstream_not_subscribed_to_window_and_downstream_disposed() {
        val observer = upstream.window(count = 2, skip = 3).test()

        upstream.onNext(1)
        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_windows_is_non_overlapping_upstream_produces_value_and_downstream_subscribed_to_window_and_downstream_disposed() {
        val observer = window(count = 2, skip = 3)

        upstream.onNext(0)
        observer.dispose()

        assertTrue(upstream.hasSubscribers)
    }
}
