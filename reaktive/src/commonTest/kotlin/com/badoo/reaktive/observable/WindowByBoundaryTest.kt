package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.SharedList
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class WindowByBoundaryTest : ObservableToObservableTests by ObservableToObservableTestsImpl({ window(TestObservable<Unit>()) }) {

    private val upstream = TestObservable<Int?>()
    private val boundaries = TestObservable<Unit>()

    @Test
    fun all_windows_emit_non_overlapping_values_WHEN_restartOnLimit_is_false_and_upstream_produced_values() {
        val downstream = upstream.window(boundaries = boundaries, limit = 5, restartOnLimit = false)
        val windows = SharedList<TestObservableObserver<Int?>>()

        downstream.subscribe(
            object : ObservableObserver<Observable<Int?>> by TestObservableObserver() {
                override fun onNext(value: Observable<Int?>) {
                    windows += value.test()
                }
            }
        )

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        assertEquals(3, windows.size)
        windows[0].assertValues(0, null, 1)
        windows[1].assertValues(2, null, 3, null, 4)
        windows[2].assertValues(7, null, 8)
    }

    @Test
    fun all_windows_emit_non_overlapping_values_WHEN_restartOnLimit_is_true_and_upstream_produced_values() {
        val downstream = upstream.window(boundaries = boundaries, limit = 5, restartOnLimit = true)
        val windows = SharedList<TestObservableObserver<Int?>>()

        downstream.subscribe(
            object : ObservableObserver<Observable<Int?>> by TestObservableObserver() {
                override fun onNext(value: Observable<Int?>) {
                    windows += value.test()
                }
            }
        )

        upstream.onNext(0, null, 1)
        boundaries.onNext(Unit)
        upstream.onNext(2, null, 3, null, 4, null, 5, null, 6)
        boundaries.onNext(Unit)
        upstream.onNext(7, null, 8)

        assertEquals(4, windows.size)
        windows[0].assertValues(0, null, 1)
        windows[1].assertValues(2, null, 3, null, 4)
        windows[2].assertValues(null, 5, null, 6)
        windows[3].assertValues(7, null, 8)
    }

    @Test
    fun subscribes_to_boundaries_WHEN_subscribed() {
        window()

        assertTrue(boundaries.hasSubscribers)
    }

    @Test
    fun first_window_emits_all_values_from_upstream_in_order_WHEN_subscribed_after_upstream_produced_values() {
        val observer = window()

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun first_window_emits_all_values_from_upstream_in_order_WHEN_subscribed_and_upstream_produced_values() {
        val windowObserver = window().subscribeLastWindow()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun window_produces_error_WHEN_subscribed_second_time() {
        val observer = window()
        observer.subscribeLastWindow()

        val windowObserver = observer.subscribeLastWindow()

        windowObserver.assertError()
    }

    @Test
    fun first_window_emits_all_values_from_upstream_in_order_for_first_subscription_WHEN_subscribed_twice_and_upstream_produced_values() {
        val observer = window()

        val windowObserver = window().subscribeLastWindow()
        observer.subscribeLastWindow()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun emits_second_window_WHEN_first_boundary_emitted() {
        val observer = window()
        observer.reset()

        boundaries.onNext(Unit)

        observer.assertSingleWindow()
    }

    @Test
    fun does_not_emit_second_window_WHEN_restartOnLimit_is_false_and_first_window_reached_limit() {
        val observer = window(limit = 5, restartOnLimit = false)
        observer.reset()

        upstream.onNext(0, null, 1, null)
        observer.reset()
        upstream.onNext(2)

        observer.assertNoValues()
    }

    @Test
    fun emits_second_window_WHEN_restartOnLimit_is_true_and_first_window_reached_limit() {
        val observer = window(limit = 5, restartOnLimit = true)
        observer.reset()

        upstream.onNext(0, null, 1, null)
        observer.reset()
        upstream.onNext(2)

        observer.assertSingleWindow()
    }

    @Test
    fun emits_second_window_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_and_first_boundary_emitted() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1, null, 2)
        observer.reset()
        boundaries.onNext(Unit)

        observer.assertSingleWindow()
    }

    @Test
    fun completes_first_window_WHEN_first_boundary_emitted() {
        val windowObserver = window().subscribeLastWindow()

        boundaries.onNext(Unit)

        windowObserver.assertComplete()
    }

    @Test
    fun completes_first_window_WHEN_restartOnLimit_is_false_and_first_window_reached_limit() {
        val windowObserver = window(limit = 5, restartOnLimit = false).subscribeLastWindow()

        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    @Test
    fun completes_first_window_WHEN_restartOnLimit_is_true_and_first_window_reached_limit() {
        val windowObserver = window(limit = 5, restartOnLimit = true).subscribeLastWindow()

        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_emits_all_values_from_upstream_in_order_WHEN_subscribed_after_upstream_produced_values() {
        val observer = window()

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun second_window_emits_all_values_from_upstream_in_order_WHEN_first_boundary_emitted_and_upstream_produced_values() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun second_window_emits_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_and_first_boundary_emitted_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun second_window_emits_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun second_window_does_not_emit_values_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_upstream_produced_values_and_first_boundary_emitted() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        upstream.onNext(3, null, 4)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()

        windowObserver.assertNoValues()
    }

    @Test
    fun first_window_does_not_emit_values_WHEN_first_boundary_emitted_and_upstream_produced_values() {
        val windowObserver = window().subscribeLastWindow()

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertNoValues()
    }

    @Test
    fun first_window_does_not_emit_values_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_and_upstream_produced_values() {
        val windowObserver = window(limit = 5, restartOnLimit = false).subscribeLastWindow()

        upstream.onNext(0, null, 1, null, 2)
        windowObserver.reset()
        upstream.onNext(3, null, 4)

        windowObserver.assertNoValues()
    }

    @Test
    fun first_window_does_not_emit_values_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_upstream_produced_values() {
        val windowObserver = window(limit = 5, restartOnLimit = true).subscribeLastWindow()

        upstream.onNext(0, null, 1, null, 2)
        windowObserver.reset()
        upstream.onNext(3, null, 4)

        windowObserver.assertNoValues()
    }

    @Test
    fun second_window_completes_WHEN_first_boundary_emitted_and_second_window_reached_limit() {
        val observer = window(limit = 5)

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_first_window_reached_limit_and_first_boundary_emitted() {
        val observer = window(limit = 5)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onNext(Unit)

        windowObserver.assertComplete()
    }

    @Test
    fun does_not_emit_third_window_WHEN_restartOnLimit_is_false_and_first_boundary_emitted_and_second_window_reached_limit() {
        val observer = window(limit = 5, restartOnLimit = false)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null)
        observer.reset()
        upstream.onNext(2)

        observer.assertNoValues()
    }

    @Test
    fun emits_third_window_WHEN_restartOnLimit_is_false_and_first_boundary_emitted_and_second_window_reached_limit_and_second_boundary_emitted() {
        val observer = window(limit = 5, restartOnLimit = false)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        observer.reset()
        boundaries.onNext(Unit)

        observer.assertSingleWindow()
    }

    @Test
    fun emits_third_window_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null)
        observer.reset()
        upstream.onNext(2)

        observer.assertSingleWindow()
    }

    @Test
    fun emits_third_window_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        observer.reset()
        boundaries.onNext(Unit)

        observer.assertSingleWindow()
    }

    @Test
    fun emits_third_window_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_second_window_reached_limit() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2, 3, null, 4, null)
        observer.reset()
        upstream.onNext(5)

        observer.assertSingleWindow()
    }

    @Test
    fun completes_second_window_WHEN_restartOnLimit_is_false_and_first_boundary_emitted_and_second_window_reached_limit() {
        val observer = window(limit = 5, restartOnLimit = false)

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    @Test
    fun completes_second_window_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    @Test
    fun completes_second_window_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onNext(Unit)

        windowObserver.assertComplete()
    }

    @Test
    fun third_window_emits_all_values_from_upstream_in_order_WHEN_subscribed_after_upstream_produced_values() {
        val observer = window()

        boundaries.onNext(Unit)
        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun third_window_emits_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_and_first_boundary_emitted_and_second_boundary_emitted_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun third_window_emits_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun third_window_emits_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun third_window_does_not_emit_values_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_upstream_produced_values_and_second_boundary_received() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        upstream.onNext(3, null, 4)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()

        windowObserver.assertNoValues()
    }

    @Test
    fun second_window_does_not_emit_values_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_and_first_boundary_emitted_and_second_boundary_emitted_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onNext(Unit)
        upstream.onNext(3, null, 4)

        windowObserver.assertNoValues()
    }

    @Test
    fun second_window_does_not_emit_values_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onNext(Unit)
        upstream.onNext(3, null, 4)

        windowObserver.assertNoValues()
    }

    @Test
    fun second_window_does_not_emit_values_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onNext(0, null, 1, null, 2)
        windowObserver.reset()
        upstream.onNext(3, null, 4)

        windowObserver.assertNoValues()
    }

    @Test
    fun first_window_completes_WHEN_boundaries_completed() {
        val windowObserver = window().subscribeLastWindow()

        boundaries.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_first_boundary_emitted_and_boundaries_completed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_and_first_boundary_emitted_and_boundaries_completed() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_boundaries_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun third_window_completes_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_boundaries_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun third_window_completes_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_boundaries_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun first_window_completes_WHEN_upstream_completed() {
        val windowObserver = window().subscribeLastWindow()

        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_first_boundary_emitted_and_upstream_completed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_restartOnLimit_is_false_and_first_window_reached_limit_first_boundary_emitted_and_and_upstream_completed() {
        val observer = window(limit = 5, restartOnLimit = false)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_upstream_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun third_window_completes_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_upstream_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun third_window_completes_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_upstream_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onComplete()

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
        val windowObserver = observer.subscribeLastWindow()

        observer.dispose()

        windowObserver.assertNotComplete()
    }

    @Test
    fun second_window_does_not_complete_WHEN_first_boundary_emitted_and_downstream_disposed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()

        windowObserver.assertNotComplete()
    }

    @Test
    fun second_window_does_not_complete_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_downstream_disposed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()

        windowObserver.assertNotComplete()
    }

    @Test
    fun third_window_does_not_complete_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_downstream_disposed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()

        windowObserver.assertNotComplete()
    }

    @Test
    fun third_window_does_not_complete_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_downstream_disposed() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()

        windowObserver.assertNotComplete()
    }

    @Test
    fun first_window_receives_all_values_from_upstream_in_order_WHEN_downstream_disposed_and_upstream_produced_values() {
        val observer = window()
        val windowObserver = observer.subscribeLastWindow()

        observer.dispose()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun second_window_receives_all_values_from_upstream_in_order_WHEN_first_boundary_emitted_and_downstream_disposed_and_upstream_produced_values() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertValues(0, null, 1, null, 2)
    }

    @Test
    fun second_window_receives_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_downstream_disposed_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun third_window_receives_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_downstream_disposed_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun third_window_receives_all_values_from_upstream_in_order_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_downstream_disposed_and_upstream_produced_values() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onNext(3, null, 4)

        windowObserver.assertValues(3, null, 4)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_downstream_disposed_and_first_window_disposed() {
        val observer = window()
        val windowObserver = observer.subscribeLastWindow()

        observer.dispose()
        windowObserver.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_first_boundary_emitted_and_downstream_disposed_and_second_window_disposed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        windowObserver.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_downstream_disposed_and_second_window_disposed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        windowObserver.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_downstream_disposed_and_third_window_disposed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        windowObserver.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun unsubscribes_from_upstream_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_downstream_disposed_and_third_window_disposed() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        windowObserver.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    @Test
    fun first_window_completes_WHEN_downstream_disposed_and_upstream_completed() {
        val observer = window()
        val windowObserver = observer.subscribeLastWindow()

        observer.dispose()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_first_boundary_emitted_and_downstream_disposed_and_upstream_completed() {
        val observer = window()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun second_window_completes_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_downstream_disposed_and_upstream_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun third_window_completes_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_first_boundary_emitted_and_downstream_disposed_and_upstream_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        upstream.onNext(0, null, 1, null, 2)
        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun third_window_completes_WHEN_restartOnLimit_is_true_and_first_boundary_emitted_and_second_window_reached_limit_and_downstream_disposed_and_upstream_completed() {
        val observer = window(limit = 5, restartOnLimit = true)

        boundaries.onNext(Unit)
        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        observer.dispose()
        upstream.onComplete()

        windowObserver.assertComplete()
    }

    @Test
    fun first_window_produces_error_WHEN_upstream_produced_error() {
        val observer = window()
        val windowObserver = observer.subscribeLastWindow()
        val error = Exception()

        upstream.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun second_window_produces_error_WHEN_first_boundary_emitted_and_upstream_produced_error() {
        val observer = window()
        val error = Exception()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun second_window_produces_error_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_upstream_produced_error() {
        val observer = window(limit = 5, restartOnLimit = true)
        val error = Exception()

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        upstream.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun first_window_produces_error_WHEN_boundaries_produced_error() {
        val observer = window()
        val windowObserver = observer.subscribeLastWindow()
        val error = Exception()

        boundaries.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun second_window_produces_error_WHEN_first_boundary_emitted_and_boundaries_produced_error() {
        val observer = window()
        val error = Exception()

        boundaries.onNext(Unit)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun second_window_produces_error_WHEN_restartOnLimit_is_true_and_first_window_reached_limit_and_boundaries_produced_error() {
        val observer = window(limit = 5, restartOnLimit = true)
        val error = Exception()

        upstream.onNext(0, null, 1, null, 2)
        val windowObserver = observer.subscribeLastWindow()
        boundaries.onError(error)

        windowObserver.assertError(error)
    }

    @Test
    fun produces_error_WHEN_boundaries_produced_error() {
        val observer = window()
        val error = Error()

        boundaries.onError(error)

        observer.assertError(error)
    }

    private fun window(limit: Long = Long.MAX_VALUE, restartOnLimit: Boolean = false): TestObservableObserver<Observable<Int?>> =
        upstream
            .window(boundaries = boundaries, limit = limit, restartOnLimit = restartOnLimit)
            .test()

    private fun TestObservableObserver<Observable<Int?>>.assertSingleWindow() {
        assertEquals(1, values.size)
    }

    private fun TestObservableObserver<Observable<Int?>>.subscribeLastWindow(): TestObservableObserver<Int?> =
        values.last().test()
}
