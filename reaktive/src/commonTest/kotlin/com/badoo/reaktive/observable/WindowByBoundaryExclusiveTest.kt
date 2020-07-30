package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.completableOfNever
import com.badoo.reaktive.test.base.hasSubscribers
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.TestObservableObserver
import com.badoo.reaktive.test.observable.assertComplete
import com.badoo.reaktive.test.observable.onNext
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlin.test.Test
import kotlin.test.assertFalse

class WindowByBoundaryExclusiveTest :
    WindowByBoundaryGenericTests by WindowByBoundaryGenericTestsImpl(
        { upstream, boundary -> upstream.window(boundary, isExclusive = true) }
    ),
    ObservableToObservableTests by ObservableToObservableTestsImpl(
        { window(observable { it.onNext(WindowBoundary(completableOfNever())) }, isExclusive = true) }
    ) {

    private val upstream = TestObservable<Int?>()
    private val boundary = TestObservable<WindowBoundary>()
    private val closingSignal = TestCompletable()
    private val observer = upstream.window(boundary, isExclusive = true).test()

    @Test
    fun previous_window_completes_WHEN_new_boundary_received() {
        boundary.onNext(WindowBoundary())
        val windowObserver = requireSingleWindow().test()

        boundary.onNext(WindowBoundary())

        windowObserver.assertComplete()
    }

    @Test
    fun old_window_completes_before_new_window_emitted_WHEN_new_boundary_received() {
        boundary.onNext(WindowBoundary())

        var isNewWindowEmitted by AtomicBoolean()

        requireSingleWindow().subscribe(
            object : ObservableObserver<Int?> by TestObservableObserver() {
                override fun onComplete() {
                    isNewWindowEmitted = observer.values.isNotEmpty()
                }
            }
        )

        observer.reset()

        boundary.onNext(WindowBoundary())

        assertFalse(isNewWindowEmitted)
    }

    @Test
    fun previous_window_completes_WHEN_restartOnLimit_is_true_and_limit_reached_and_new_boundary_received() {
        boundary.onNext(WindowBoundary(limit = 5, restartOnLimit = true))
        observer.reset()
        upstream.onNext(0, null, 1, null, 2)
        val previousWindowObserver = requireSingleWindow().test()

        boundary.onNext(WindowBoundary())

        previousWindowObserver.assertComplete()
    }

    @Test
    fun unsubscribes_from_previous_closing_signal_WHEN_new_boundary_received() {
        boundary.onNext(WindowBoundary(closingSignal = closingSignal))

        boundary.onNext(WindowBoundary())

        assertFalse(closingSignal.hasSubscribers)
    }

    private fun requireSingleWindow(): Observable<Int?> =
        observer.values.single()
}
