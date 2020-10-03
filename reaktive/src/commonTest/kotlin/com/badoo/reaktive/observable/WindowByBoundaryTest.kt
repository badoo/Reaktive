package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.asserter

class WindowByBoundaryTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ window(TestObservable<Unit>()) }) {

    private val upstream = TestObservable<Int?>()
    private val boundaries = TestObservable<Unit>()

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_false_and_upstream_produced_values_and_windows_not_abandoned() {
        val observer = window(limit = 5, restartOnLimit = false)

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        val windowCount4 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount5 = observer.values.size
        upstream.onNext(7, null, 8)
        val windowCount6 = observer.values.size

        assertEquals(1, windowCount1)
        assertEquals(1, windowCount2)
        assertEquals(2, windowCount3)
        assertEquals(2, windowCount4)
        assertEquals(3, windowCount5)
        assertEquals(3, windowCount6)
    }

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_true_and_upstream_produced_values_and_windows_not_abandoned() {
        val observer = window(limit = 5, restartOnLimit = true)

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        val windowCount4 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount5 = observer.values.size
        upstream.onNext(7, null, 8)
        val windowCount6 = observer.values.size

        assertEquals(1, windowCount1)
        assertEquals(1, windowCount2)
        assertEquals(2, windowCount3)
        assertEquals(3, windowCount4)
        assertEquals(4, windowCount5)
        assertEquals(4, windowCount6)
    }

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_false_and_upstream_produced_values_and_windows_abandoned() {
        val observer = window(limit = 5, restartOnLimit = false) {}

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        val windowCount4 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount5 = observer.values.size
        upstream.onNext(7, null, 8)
        val windowCount6 = observer.values.size

        assertEquals(1, windowCount1)
        assertEquals(1, windowCount2)
        assertEquals(2, windowCount3)
        assertEquals(2, windowCount4)
        assertEquals(3, windowCount5)
        assertEquals(3, windowCount6)
    }

    @Test
    fun emits_all_windows_correctly_WHEN_restartOnLimit_is_true_and_upstream_produced_values_and_windows_abandoned() {
        val observer = window(limit = 5, restartOnLimit = true) {}

        val windowCount1 = observer.values.size
        upstream.onNext(0, null, 1)
        val windowCount2 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount3 = observer.values.size
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        val windowCount4 = observer.values.size
        boundaries.onNext(Unit)
        val windowCount5 = observer.values.size
        upstream.onNext(7, null, 8)
        val windowCount6 = observer.values.size

        assertEquals(1, windowCount1)
        assertEquals(1, windowCount2)
        assertEquals(2, windowCount3)
        assertEquals(2, windowCount4)
        assertEquals(3, windowCount5)
        assertEquals(3, windowCount6)
    }

    @Test
    fun all_windows_emit_non_overlapping_values_WHEN_restartOnLimit_is_false_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        observer.values[0].assertValues(0, null, 1)
        observer.values[1].assertValues(2, null, 3, null, 4)
        observer.values[2].assertValues(7, null, 8)
    }

    @Test
    fun all_windows_emit_non_overlapping_values_WHEN_restartOnLimit_is_true_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        observer.values[0].assertValues(0, null, 1)
        observer.values[1].assertValues(2, null, 3, null, 4)
        observer.values[2].assertValues(null, 5, null, 6)
        observer.values[3].assertValues(7, null, 8)
    }

    @Test
    fun all_closed_windows_complete_WHEN_restartOnLimit_is_false_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        observer.values.dropLast(1).forEach {
            it.assertComplete()
        }
    }

    @Test
    fun all_closed_windows_complete_WHEN_restartOnLimit_is_true_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        observer.values.dropLast(1).forEach {
            it.assertComplete()
        }
    }

    @Test
    fun all_abandoned_windows_complete_WHEN_restartOnLimit_is_false_and_upstream_produced_values_and_abandoned_windows_subscribed() {
        val observer = window(limit = 5, restartOnLimit = false) {}

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        observer.values.forEach {
            it.test().assertComplete()
        }
    }

    @Test
    fun all_abandoned_windows_complete_WHEN_restartOnLimit_is_true_and_upstream_produced_values_and_abandoned_windows_subscribed() {
        val observer = window(limit = 5, restartOnLimit = true) {}

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        observer.values.forEach {
            it.test().assertComplete()
        }
    }

    @Test
    fun subscribes_to_boundaries_WHEN_subscribed() {
        window()

        assertTrue(boundaries.hasSubscribers)
    }

    @Test
    fun emits_first_window_WHEN_subscribed() {
        val observer = window()

        observer.assertSingleWindow()
    }

    @Test
    fun window_produces_error_WHEN_subscribed_second_time() {
        val observer = window { it.test() }

        val windowObserver = observer.lastValue().test()

        windowObserver.assertError()
    }

    @Test
    fun first_window_emits_all_values_from_upstream_in_order_for_first_subscription_WHEN_subscribed_twice_and_upstream_produced_values() {
        var windowObserver by AtomicReference<TestObservableObserver<Int?>?>(null)

        val observer = window { windowObserver = it.test() }
        observer.lastValue().test()
        upstream.onNext(0, null, 1, null, 2)

        requireNotNull(windowObserver).assertValues(0, null, 1, null, 2)
    }

    @Test
    fun emits_second_window_WHEN_first_boundary_emitted() {
        val observer = window()
        observer.reset()

        boundaries.onNext(Unit)

        observer.assertSingleWindow()
    }

    @Test
    fun completes_first_window_WHEN_first_boundary_emitted() {
        val windowObserver = window().lastValue()

        boundaries.onNext(Unit)

        windowObserver.assertComplete()
    }

    @Test
    fun completes_first_window_WHEN_restartOnLimit_is_false_and_first_window_reached_limit() {
        val windowObserver = window(limit = 5, restartOnLimit = false).lastValue()

        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    @Test
    fun completes_first_window_WHEN_restartOnLimit_is_true_and_first_window_reached_limit() {
        val windowObserver = window(limit = 5, restartOnLimit = true).lastValue()

        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    @Test
    fun unsubscribes_from_boundaries_WHEN_upstream_completed() {
        window()

        upstream.onComplete()

        assertFalse(boundaries.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_boundaries_completed() {
        window()

        boundaries.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_boundaries_WHEN_downstream_disposed() {
        val observer = window()

        observer.dispose()

        assertFalse(boundaries.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_boundaries_WHEN_first_boundary_emitted_and_downstream_disposed() {
        val observer = window()

        boundaries.onNext(Unit)
        observer.dispose()

        assertFalse(boundaries.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_boundaries_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_and_downstream_disposed() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1, null, 2)
        observer.dispose()

        assertFalse(boundaries.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_boundaries_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_downstream_disposed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        observer.dispose()

        assertFalse(boundaries.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_boundaries_WHEN_first_boundary_emitted() {
        window()

        boundaries.onNext(Unit)

        assertTrue(boundaries.hasSubscribers)
    }

    @Ignore
    override fun unsubscribes_from_upstream_WHEN_disposed() {
        // Not applicable
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_downstream_disposed() {
        val observer = window()

        observer.dispose()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_first_boundary_emitted() {
        window()

        boundaries.onNext(Unit)

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_first_boundary_emitted_and_downstream_disposed() {
        val observer = window()

        boundaries.onNext(Unit)
        observer.dispose()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun first_window_does_not_complete_WHEN_downstream_disposed() {
        val observer = window()
        val windowObserver = observer.lastValue()

        observer.dispose()

        windowObserver.assertNotComplete()
    }

    @Test
    fun second_window_does_not_complete_WHEN_first_boundary_emitted_and_downstream_disposed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.lastValue()
        observer.dispose()

        windowObserver.assertNotComplete()
    }

    @Test
    fun first_window_receives_all_values_from_upstream_in_order_WHEN_downstream_disposed_and_upstream_produced_values() {
        val observer = window()
        val windowObserver = observer.lastValue()

        observer.dispose()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun second_window_receives_all_values_from_upstream_in_order_WHEN_first_boundary_emitted_and_downstream_disposed_and_upstream_produced_values() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.lastValue()
        observer.dispose()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_downstream_disposed_and_first_window_disposed() {
        val observer = window()
        val windowObserver = observer.lastValue()

        observer.dispose()
        windowObserver.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_first_boundary_emitted_and_downstream_disposed_and_second_window_disposed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.lastValue()
        observer.dispose()
        windowObserver.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun does_not_unsubscribe_from_upstream_WHEN_first_window_disposed() {
        val observer = window()
        boundaries.onNext(Unit)
        val windowObserver = observer.lastValue()

        windowObserver.dispose()

        assertTrue(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_first_window_disposed_and_boundaries_completed() {
        val observer = window()
        boundaries.onNext(Unit)
        val windowObserver = observer.lastValue()

        windowObserver.dispose()
        boundaries.onComplete()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun first_window_completes_WHEN_downstream_disposed_and_upstream_completed() {
        val observer = window()
        val windowObserver = observer.lastValue()

        observer.dispose()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_first_boundary_emitted_and_downstream_disposed_and_upstream_completed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.lastValue()
        observer.dispose()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun first_window_produces_error_WHEN_upstream_produced_error() {
        val observer = window()
        val windowObserver = observer.lastValue()
        val error = Exception()

        upstream.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun second_window_produces_error_WHEN_first_boundary_emitted_and_upstream_produced_error() {
        val observer = window()
        val error = Exception()

        boundaries.onNext(Unit)
        val windowObserver = observer.lastValue()
        upstream.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun produces_error_WHEN_boundaries_produced_error() {
        val observer = window()
        val error = Error()

        boundaries.onError(error)

        observer.assertError(error)
    }

    private fun window(
        limit: Long = Long.MAX_VALUE,
        restartOnLimit: Boolean = false,
        onNext: (Observable<Int?>) -> Unit
    ): TestObservableObserver<Observable<Int?>> =
        upstream
            .window(boundaries = boundaries, limit = limit, restartOnLimit = restartOnLimit)
            .doOnBeforeNext(onNext)
            .test()

    private fun window(
        limit: Long = Long.MAX_VALUE,
        restartOnLimit: Boolean = false
    ): TestObservableObserver<TestObservableObserver<Int?>> =
        upstream
            .window(boundaries = boundaries, limit = limit, restartOnLimit = restartOnLimit)
            .map { it.test() }
            .test()

    private fun TestObservableObserver<*>.assertSingleWindow() {
        assertEquals(1, values.size)
    }

    private fun <T> TestObservableObserver<T>.lastValue(): T = values.last()
}
