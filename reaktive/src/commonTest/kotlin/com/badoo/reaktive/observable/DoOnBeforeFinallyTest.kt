package com.badoo.reaktive.observable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoOnBeforeFinallyTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ doOnBeforeFinally {} }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ doOnBeforeFinally {} }) {

    private val upstream = TestObservable<Int>()

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun calls_action_before_completion() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("action", "onComplete"), callOrder)
    }

    @Test
    fun calls_action_before_failing() {
        val callOrder = ArrayList<String>()
        val exception = Exception()

        upstream
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action", "onError"), callOrder)
    }

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = ArrayList<String>()

        observableUnsafe<Unit> { observer ->
            observer.onSubscribe(
                Disposable {
                    callOrder += "dispose"
                }
            )
        }
            .doOnBeforeFinally {
                callOrder += "action"
            }
            .test()
            .dispose()

        assertEquals(listOf("action", "dispose"), callOrder)
    }

    @Test
    fun calls_action_WHEN_disposed_before_upstream_onSubscribe() {
        var isCalled = false

        observableUnsafe<Nothing> {}
            .doOnBeforeFinally { isCalled = true }
            .test()
            .dispose()

        assertTrue(isCalled)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_completed() {
        var count = 0

        upstream
            .doOnBeforeFinally { count++ }
            .test()
            .dispose()

        upstream.onComplete()

        assertEquals(1, count)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_produced_error() {
        var count = 0

        upstream
            .doOnBeforeFinally { count++ }
            .test()
            .dispose()

        upstream.onError(Throwable())

        assertEquals(1, count)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_upstream_completed_and_downstream_disposed() {
        var count = 0

        val observer =
            upstream
                .doOnBeforeFinally { count++ }
                .test()

        upstream.onComplete()
        observer.dispose()

        assertEquals(1, count)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_upstream_produced_error_and_downstream_disposed() {
        var count = 0

        val observer =
            upstream
                .doOnBeforeFinally { count++ }
                .test()

        upstream.onError(Throwable())
        observer.dispose()

        assertEquals(1, count)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_emitted_value() {
        var isCalled = false

        upstream
            .doOnBeforeFinally { isCalled = true }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled)
    }

    @Test
    fun produces_error_WHEN_upstream_completed_and_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeFinally { throw error }
                .test()

        upstream.onComplete()

        observer.assertError(error)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_downstream_disposed_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeFinally { throw error }
                .test()

        observer.dispose()

        assertSame(error, caughtException.value)
    }

    @Test
    fun disposes_upstream_WHEN_downstream_disposed_and_exception_in_lambda() {
        mockUncaughtExceptionHandler()
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeFinally { throw error }
                .test()

        observer.dispose()

        observer.assertDisposed()
    }

    @Test
    fun produces_CompositeException_WHEN_upstream_produced_error_and_exception_in_lambda() {
        val error1 = Exception()
        val error2 = Exception()

        val observer =
            upstream
                .doOnBeforeFinally { throw error2 }
                .test()

        upstream.onError(error1)

        val error: Throwable? = observer.error
        assertTrue(error is CompositeException)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }
}
