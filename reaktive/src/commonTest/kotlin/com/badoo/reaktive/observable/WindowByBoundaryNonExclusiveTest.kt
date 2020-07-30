package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.completableOfNever
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.assertNotComplete
import com.badoo.reaktive.test.observable.assertValues
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertEquals

class WindowByBoundaryNonExclusiveTest :
    WindowByBoundaryGenericTests by WindowByBoundaryGenericTestsImpl(
        { upstream, boundary -> upstream.window(boundary, isExclusive = false) }
    ),
    ObservableToObservableTests by ObservableToObservableTestsImpl(
        { window(observable { it.onNext(WindowBoundary(completableOfNever())) }, isExclusive = false) }
    ) {

    private val upstream = TestObservable<Int?>()
    private val boundary = TestObservable<WindowBoundary>()
    private val closingSignal = TestCompletable()
    private val observer = upstream.window(boundary, isExclusive = false).test()

    @Test
    fun closed_windows_complete_WHEN_some_boundaries_closed() {
        val closingSignal1 = TestCompletable()
        val closingSignal3 = TestCompletable()

        boundary.onNext(WindowBoundary(closingSignal1))
        val windowObserver1 = requireSingleWindow().test()
        boundary.onNext(WindowBoundary(TestCompletable()))
        observer.reset()
        boundary.onNext(WindowBoundary(closingSignal3))
        val windowObserver3 = requireSingleWindow().test()

        closingSignal1.onComplete()
        closingSignal3.onComplete()

        windowObserver1.assertComplete()
        windowObserver3.assertComplete()
    }

    @Test
    fun non_closed_windows_do_not_complete_WHEN_some_boundaries_closed() {
        val closingSignal2 = TestCompletable()

        boundary.onNext(WindowBoundary(TestCompletable()))
        val windowObserver1 = requireSingleWindow().test()
        boundary.onNext(WindowBoundary(closingSignal2))
        observer.reset()
        boundary.onNext(WindowBoundary(TestCompletable()))
        val windowObserver3 = requireSingleWindow().test()

        closingSignal2.onComplete()

        windowObserver1.assertNotComplete()
        windowObserver3.assertNotComplete()
    }

    @Test
    fun previous_window_does_not_complete_WHEN_new_boundary_received() {
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()

        boundary.onNext(WindowBoundary())

        windowObserver.assertNotComplete()
    }

    @Test
    fun previous_window_receives_new_values_WHEN_new_boundary_received_and_upstream_produced_values() {
        upstream.onNext(0, null, 1)
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()
        upstream.onNext(2, null, 3)

        boundary.onNext(WindowBoundary())
        val newValues = arrayOf(4, null, 5, null, 6, null, 7)
        windowObserver.reset()
        upstream.onNext(*newValues)

        windowObserver.assertValues(*newValues)
    }

    @Test
    fun previous_window_does_not_complete_WHEN_restartOnLimit_is_true_and_limit_reached_and_new_non_exclusive_boundary_received() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))
        observer.reset()
        upstream.onNext(0, null, 1, null, 2)
        val previousWindowObserver = requireSingleWindow().test()

        boundary.onNext(WindowBoundary())

        previousWindowObserver.assertNotComplete()
    }

    @Test
    fun all_active_windows_complete_WHEN_boundary_stream_completed() {
        boundary.onNext(WindowBoundary())
        val windowObserver1 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver2 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver3 = requireSingleWindow().test()

        boundary.onComplete()

        windowObserver1.assertComplete()
        windowObserver2.assertComplete()
        windowObserver3.assertComplete()
    }

    @Test
    fun all_active_windows_complete_WHEN_boundary_stream_produced_error() {
        boundary.onNext(WindowBoundary())
        val windowObserver1 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver2 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver3 = requireSingleWindow().test()

        boundary.onError(Exception())

        windowObserver1.assertComplete()
        windowObserver2.assertComplete()
        windowObserver3.assertComplete()
    }

    @Test
    fun all_active_windows_complete_WHEN_upstream_completed() {
        boundary.onNext(WindowBoundary())
        val windowObserver1 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver2 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver3 = requireSingleWindow().test()

        upstream.onComplete()

        windowObserver1.assertComplete()
        windowObserver2.assertComplete()
        windowObserver3.assertComplete()
    }

    @Test
    fun all_active_windows_complete_WHEN_upstream_produced_error() {
        boundary.onNext(WindowBoundary())
        val windowObserver1 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver2 = requireSingleWindow().test()
        observer.reset()
        boundary.onNext(WindowBoundary())
        val windowObserver3 = requireSingleWindow().test()

        upstream.onError(Exception())

        windowObserver1.assertComplete()
        windowObserver2.assertComplete()
        windowObserver3.assertComplete()
    }

    @Test
    fun does_not_restart_second_window_WHEN_restartOnLimit_is_true_and_limit_reached_and_second_boundary_completed_while_first_window_emits_value() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))
        val firstWindow = requireSingleWindow()
        observer.reset()
        boundary.onNext(WindowBoundary(closingSignal, limit = 5, restartOnLimit = true))
        upstream.onNext(0, null, 1, null)
        observer.reset()

        firstWindow.subscribe(
            object : ObservableObserver<Int?> by TestObservableObserver() {
                override fun onNext(value: Int?) {
                    closingSignal.onComplete()
                }
            }
        )

        upstream.onNext(2)

        assertEquals(1, observer.values.size)
    }

    private fun requireSingleWindow(): Observable<Int?> =
        observer.values.single()
}
