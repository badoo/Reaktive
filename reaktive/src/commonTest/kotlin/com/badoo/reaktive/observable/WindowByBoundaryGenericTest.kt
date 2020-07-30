package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.completableUnsafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNoValues
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.AtomicInt
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

interface WindowByBoundaryGenericTests {

    @Test
    fun subscribes_to_boundary_WHEN_subscribed()

    @Test
    fun subscribes_to_boundary_before_subscribing_to_upstream_WHEN_subscribed()

    @Test
    fun does_not_emit_window_WHEN_subscribed_and_no_boundary_received()

    @Test
    fun emits_window_WHEN_subscribed_and_boundary_received()

    @Test
    fun window_does_not_complete_WHEN_window_emitted_and_subscribed()

    @Test
    fun window_completes_WHEN_closing_signal_completed()

    @Test
    fun new_window_emitted_WHEN_new_boundary_received()

    @Test
    fun window_emits_new_values_from_upstream_in_order_WHEN_boundary_received_and_upstream_produced_values()

    @Test
    fun new_window_emits_new_values_from_upstream_in_order_WHEN_boundary_received_and_upstream_produced_values()

    @Test
    fun window_does_not_emit_extra_values_WHEN_limit_reached()

    @Test
    fun window_completes_WHEN_restartOnLimit_is_true_and_limit_reached()

    @Test
    fun window_completes_WHEN_restartOnLimit_is_false_and_limit_reached()

    @Test
    fun emits_new_window_WHEN_restartOnLimit_is_true_and_limit_reached()

    @Test
    fun does_not_emit_new_window_WHEN_restartOnLimit_is_false_and_limit_reached()

    @Test
    fun emits_new_window_WHEN_restartOnLimit_is_true_and_limit_reached_and_new_boundary_received()

    @Test
    fun emits_new_window_WHEN_restartOnLimit_is_false_and_limit_reached_and_new_boundary_received()

    @Test
    fun completes_WHEN_no_open_window_and_boundary_stream_completed()

    @Test
    fun completes_WHEN_window_opened_and_boundary_stream_completed()

    @Test
    fun window_completes_WHEN_boundary_stream_completed()

    @Test
    fun produces_error_WHEN_window_opened_and_boundary_stream_produced_error()

    @Test
    fun produces_error_WHEN_no_open_windows_and_boundary_stream_produced_error()

    @Test
    fun window_completes_WHEN_boundary_stream_produced_error()

    @Test
    fun unsubscribes_from_boundary_WHEN_window_opened_and_downstream_disposed()

    @Test
    fun unsubscribes_from_boundary_WHEN_no_open_windows_and_downstream_disposed()

    @Test
    fun unsubscribes_from_boundary_WHEN_upstream_completed()

    @Test
    fun unsubscribes_from_boundary_WHEN_upstream_produced_error()

    @Test
    fun completes_WHEN_window_opened_and_upstream_completed()

    @Test
    fun completes_WHEN_no_open_windows_and_upstream_completed()

    @Test
    fun window_completes_WHEN_upstream_completed()

    @Test
    fun produces_error_WHEN_window_opened_and_upstream_produced_error()

    @Test
    fun produces_error_WHEN_no_open_windows_and_upstream_produced_error()

    @Test
    fun window_completes_WHEN_upstream_produced_error()

    @Test
    fun unsubscribes_from_upstream_WHEN_window_opened_and_downstream_disposed()

    @Test
    fun unsubscribes_from_upstream_WHEN_no_open_windows_and_downstream_disposed()

    @Test
    fun window_does_not_emit_extra_value_WHEN_restartOnLimit_is_true_and_limit_reached_and_upstream_produced_value_while_window_emits_value()

    @Test
    fun window_does_not_emit_extra_value_WHEN_restartOnLimit_is_false_and_limit_reached_and_upstream_produced_value_while_window_emits_value()

    @Test
    fun new_window_emits_extra_values_WHEN_restartOnLimit_is_true_and_limit_reached_and_upstream_produced_value_while_window_emits_value()

    @Test
    fun unsubscribes_from_closing_signal_WHEN_downstream_disposed()

    @Test
    fun unsubscribes_from_closing_signal_WHEN_upstream_completed()

    @Test
    fun unsubscribes_from_closing_signal_WHEN_upstream_produced_error()

    @Test
    fun unsubscribes_from_closing_signal_WHEN_restartOnLimit_is_true_and_limit_reached()

    @Test
    fun unsubscribes_from_closing_signal_WHEN_restartOnLimit_is_false_and_limit_reached()

    @Test
    fun new_window_completes_WHEN_restartOnLimit_is_true_and_limit_reached_and_closing_signal_completed()
}

@Ignore
class WindowByBoundaryGenericTestsImpl(
    private val window: (Observable<Int?>, Observable<WindowBoundary>) -> Observable<Observable<Int?>>
) : WindowByBoundaryGenericTests {

    private val upstream = TestObservable<Int?>()
    private val boundary = TestObservable<WindowBoundary>()
    private val closingSignal = TestCompletable()
    private val observer = window(upstream, boundary).test()

    override fun subscribes_to_boundary_WHEN_subscribed() {
        assertTrue(boundary.hasSubscribers)
    }

    override fun subscribes_to_boundary_before_subscribing_to_upstream_WHEN_subscribed() {
        val upstream = TestObservable<Int?>()
        var isUpstreamSubscribed by AtomicBoolean()

        val boundary =
            object : Observable<WindowBoundary> {
                override fun subscribe(observer: ObservableObserver<WindowBoundary>) {
                    isUpstreamSubscribed = upstream.hasSubscribers
                }
            }

        window(upstream, boundary).test()

        assertFalse(isUpstreamSubscribed)
    }

    override fun does_not_emit_window_WHEN_subscribed_and_no_boundary_received() {
        observer.assertNoValues()
    }

    override fun emits_window_WHEN_subscribed_and_boundary_received() {
        boundary.onNext(WindowBoundary())

        assertEquals(1, observer.values.size)
    }

    override fun window_does_not_complete_WHEN_window_emitted_and_subscribed() {
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()

        windowObserver.assertNotComplete()
    }

    override fun window_completes_WHEN_closing_signal_completed() {
        boundary.onNext(WindowBoundary(closingSignal))
        val windowObserver = requireSingleWindow().test()

        closingSignal.onComplete()

        windowObserver.assertComplete()
    }

    override fun new_window_emitted_WHEN_new_boundary_received() {
        boundary.onNext(WindowBoundary())
        observer.reset()

        boundary.onNext(WindowBoundary())

        assertEquals(1, observer.values.size)
    }

    override fun window_emits_new_values_from_upstream_in_order_WHEN_boundary_received_and_upstream_produced_values() {
        upstream.onNext(0, null, 1)
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()
        val newValues = arrayOf(2, null, 3, null, 4, null, 5)
        upstream.onNext(*newValues)

        windowObserver.assertValues(*newValues)
    }

    override fun new_window_emits_new_values_from_upstream_in_order_WHEN_boundary_received_and_upstream_produced_values() {
        upstream.onNext(0, null, 1)
        boundary.onNext(WindowBoundary())
        upstream.onNext(2, null, 3)
        observer.reset()

        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()
        val newValues = arrayOf(4, null, 5, null, 6, null, 7)
        upstream.onNext(*newValues)

        windowObserver.assertValues(*newValues)
    }

    override fun window_does_not_emit_extra_values_WHEN_limit_reached() {
        upstream.onNext(0, null, 1)
        boundary.onNext(WindowBoundary(limit = 5))
        val windowObserver = requireSingleWindow().test()
        val newValues = arrayOf(2, null, 3, null, 4, null, 5)
        upstream.onNext(*newValues)

        windowObserver.assertValues(2, null, 3, null, 4)
    }

    override fun window_completes_WHEN_restartOnLimit_is_true_and_limit_reached() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))
        val windowObserver = requireSingleWindow().test()
        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    override fun window_completes_WHEN_restartOnLimit_is_false_and_limit_reached() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = false))
        val windowObserver = requireSingleWindow().test()

        upstream.onNext(0, null, 1, null, 2)

        windowObserver.assertComplete()
    }

    override fun emits_new_window_WHEN_restartOnLimit_is_true_and_limit_reached() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))
        upstream.onNext(0, null, 1, null)
        observer.reset()

        upstream.onNext(2)

        requireSingleWindow()
    }

    override fun does_not_emit_new_window_WHEN_restartOnLimit_is_false_and_limit_reached() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = false))
        upstream.onNext(0, null, 1, null)
        observer.reset()

        upstream.onNext(2)

        observer.assertNoValues()
    }

    override fun emits_new_window_WHEN_restartOnLimit_is_true_and_limit_reached_and_new_boundary_received() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))
        upstream.onNext(0, null, 1, null, 2)
        observer.reset()

        boundary.onNext(WindowBoundary())

        requireSingleWindow()
    }

    override fun emits_new_window_WHEN_restartOnLimit_is_false_and_limit_reached_and_new_boundary_received() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = false))
        upstream.onNext(0, null, 1, null, 2)
        observer.reset()

        boundary.onNext(WindowBoundary())

        requireSingleWindow()
    }

    override fun completes_WHEN_no_open_window_and_boundary_stream_completed() {
        boundary.onComplete()

        observer.assertComplete()
    }

    override fun completes_WHEN_window_opened_and_boundary_stream_completed() {
        boundary.onNext(WindowBoundary())

        boundary.onComplete()

        observer.assertComplete()
    }

    override fun window_completes_WHEN_boundary_stream_completed() {
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()

        boundary.onComplete()

        windowObserver.assertComplete()
    }

    override fun produces_error_WHEN_window_opened_and_boundary_stream_produced_error() {
        boundary.onNext(WindowBoundary())
        val error = Exception()

        boundary.onError(error)

        observer.assertError(error)
    }

    override fun produces_error_WHEN_no_open_windows_and_boundary_stream_produced_error() {
        val error = Exception()

        boundary.onError(error)

        observer.assertError(error)
    }

    override fun window_completes_WHEN_boundary_stream_produced_error() {
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()

        boundary.onError(Exception())

        windowObserver.assertComplete()
    }

    override fun unsubscribes_from_boundary_WHEN_window_opened_and_downstream_disposed() {
        boundary.onNext(WindowBoundary())

        observer.dispose()

        assertFalse(boundary.hasSubscribers)
    }

    override fun unsubscribes_from_boundary_WHEN_no_open_windows_and_downstream_disposed() {
        observer.dispose()

        assertFalse(boundary.hasSubscribers)
    }

    override fun unsubscribes_from_boundary_WHEN_upstream_completed() {
        upstream.onComplete()

        assertFalse(boundary.hasSubscribers)
    }

    override fun unsubscribes_from_boundary_WHEN_upstream_produced_error() {
        upstream.onError(Exception())

        assertFalse(boundary.hasSubscribers)
    }

    override fun completes_WHEN_window_opened_and_upstream_completed() {
        boundary.onNext(WindowBoundary())

        upstream.onComplete()

        observer.assertComplete()
    }

    override fun completes_WHEN_no_open_windows_and_upstream_completed() {
        upstream.onComplete()

        observer.assertComplete()
    }

    override fun window_completes_WHEN_upstream_completed() {
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()

        upstream.onComplete()

        windowObserver.assertComplete()
    }

    override fun produces_error_WHEN_window_opened_and_upstream_produced_error() {
        boundary.onNext(WindowBoundary())
        val error = Exception()

        upstream.onError(error)

        observer.assertError(error)
    }

    override fun produces_error_WHEN_no_open_windows_and_upstream_produced_error() {
        val error = Exception()

        upstream.onError(error)

        observer.assertError(error)
    }

    override fun window_completes_WHEN_upstream_produced_error() {
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()

        upstream.onError(Exception())

        windowObserver.assertComplete()
    }

    override fun unsubscribes_from_upstream_WHEN_window_opened_and_downstream_disposed() {
        boundary.onNext(WindowBoundary())

        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    override fun unsubscribes_from_upstream_WHEN_no_open_windows_and_downstream_disposed() {
        observer.dispose()

        assertFalse(upstream.hasSubscribers)
    }

    override fun window_does_not_emit_extra_value_WHEN_restartOnLimit_is_true_and_limit_reached_and_upstream_produced_value_while_window_emits_value() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))
        val window = requireSingleWindow()
        upstream.onNext(0, null, 1, null)
        var isExtraValueEmitted by AtomicBoolean()

        window.subscribe(
            object : ObservableObserver<Int?> by TestObservableObserver() {
                override fun onNext(value: Int?) {
                    if (value == 2) {
                        upstream.onNext(3)
                    } else {
                        isExtraValueEmitted = true
                    }
                }
            }
        )

        upstream.onNext(2)

        assertFalse(isExtraValueEmitted)
    }

    override fun window_does_not_emit_extra_value_WHEN_restartOnLimit_is_false_and_limit_reached_and_upstream_produced_value_while_window_emits_value() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = false))
        val window = requireSingleWindow()
        upstream.onNext(0, null, 1, null)
        var isExtraValueEmitted by AtomicBoolean()

        window.subscribe(
            object : ObservableObserver<Int?> by TestObservableObserver() {
                override fun onNext(value: Int?) {
                    if (value == 2) {
                        upstream.onNext(3)
                    } else {
                        isExtraValueEmitted = true
                    }
                }
            }
        )

        upstream.onNext(2)

        assertFalse(isExtraValueEmitted)
    }

    override fun new_window_emits_extra_values_WHEN_restartOnLimit_is_true_and_limit_reached_and_upstream_produced_value_while_window_emits_value() {
        var secondWindowObserver by AtomicReference<TestObservableObserver<Int?>?>(null)

        window(upstream, boundary).subscribe(
            object : ObservableObserver<Observable<Int?>> by TestObservableObserver() {
                private var index by AtomicInt()

                override fun onNext(value: Observable<Int?>) {
                    when (++index) {
                        1 -> subscribeToFirstWindow(value)
                        2 -> subscribeToSecondWindow(value)
                    }
                }

                private fun subscribeToFirstWindow(window: Observable<Int?>) {
                    window.subscribe(
                        object : ObservableObserver<Int?> by TestObservableObserver() {
                            override fun onNext(value: Int?) {
                                if (value == 2) {
                                    upstream.onNext(3, null, 4)
                                }
                            }
                        }
                    )
                }

                private fun subscribeToSecondWindow(window: Observable<Int?>) {
                    secondWindowObserver = window.test()
                }
            }
        )


        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))

        upstream.onNext(0, null, 1, null, 2)

        requireNotNull(secondWindowObserver).assertValues(3, null, 4)
    }

    override fun unsubscribes_from_closing_signal_WHEN_downstream_disposed() {
        boundary.onNext(WindowBoundary())

        observer.dispose()

        assertFalse(closingSignal.hasSubscribers)
    }

    override fun unsubscribes_from_closing_signal_WHEN_upstream_completed() {
        boundary.onNext(WindowBoundary())

        upstream.onComplete()

        assertFalse(closingSignal.hasSubscribers)
    }

    override fun unsubscribes_from_closing_signal_WHEN_upstream_produced_error() {
        boundary.onNext(WindowBoundary())

        upstream.onError(Exception())

        assertFalse(closingSignal.hasSubscribers)
    }

    override fun unsubscribes_from_closing_signal_WHEN_restartOnLimit_is_true_and_limit_reached() {
        val isClosingSignalSubscribedFirstTime = AtomicBoolean()
        var closingSignalDisposable by AtomicReference<Disposable?>(null)

        val closingSignal =
            completableUnsafe { observer ->
                val disposable = Disposable()
                if (isClosingSignalSubscribedFirstTime.compareAndSet(false, true)) {
                    closingSignalDisposable = disposable
                }
                observer.onSubscribe(disposable)
            }

        boundary.onNext(WindowBoundary(closingSignal = closingSignal, limit = 5, restartOnLimit = true))

        upstream.onNext(0, null, 1, null, 2)

        assertTrue(requireNotNull(closingSignalDisposable).isDisposed)
    }

    override fun unsubscribes_from_closing_signal_WHEN_restartOnLimit_is_false_and_limit_reached() {
        boundary.onNext(WindowBoundary(closingSignal = closingSignal, limit = 5, restartOnLimit = false))

        upstream.onNext(0, null, 1, null, 2)

        assertFalse(closingSignal.hasSubscribers)
    }

    override fun new_window_completes_WHEN_restartOnLimit_is_true_and_limit_reached_and_closing_signal_completed() {
        boundary.onNext(WindowBoundary(closingSignal = closingSignal, limit = 5, restartOnLimit = true))
        observer.reset()

        upstream.onNext(0, null, 1, null, 2)
        val newWindowObserver = requireSingleWindow().test()
        closingSignal.onComplete()

        newWindowObserver.assertComplete()
    }

    private fun requireSingleWindow(): Observable<Int?> =
        observer.values.single()
}
