package com.badoo.reaktive.completable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoOnAfterFinallyTest : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ doOnAfterFinally {} }) {

    private val upstream = TestCompletable()

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun calls_action_after_completion() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnAfterFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultCompletableObserver {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("onComplete", "action"), callOrder)
    }

    @Test
    fun calls_action_after_failing() {
        val callOrder = ArrayList<String>()
        val exception = Exception()

        upstream
            .doOnAfterFinally {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultCompletableObserver {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("onError", "action"), callOrder)
    }

    @Test
    fun calls_action_after_disposing_upstream() {
        val callOrder = ArrayList<String>()

        completableUnsafe { observer ->
            observer.onSubscribe(
                Disposable {
                    callOrder += "dispose"
                }
            )
        }
            .doOnAfterFinally {
                callOrder += "action"
            }
            .test()
            .dispose()

        assertEquals(listOf("dispose", "action"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_disposed_before_upstream_onSubscribe() {
        var isCalled = false

        completableUnsafe {}
            .doOnAfterFinally { isCalled = true }
            .test()
            .dispose()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_completed() {
        var count = 0

        upstream
            .doOnAfterFinally { count++ }
            .test()
            .dispose()

        upstream.onComplete()

        assertEquals(1, count)
    }

    @Test
    fun does_not_call_action_second_time_WHEN_downstream_disposed_and_upstream_produced_error() {
        var count = 0

        upstream
            .doOnAfterFinally { count++ }
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
                .doOnAfterFinally { count++ }
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
                .doOnAfterFinally { count++ }
                .test()

        upstream.onError(Throwable())
        observer.dispose()

        assertEquals(1, count)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_upstream_completed_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        upstream
            .doOnAfterFinally { throw error }
            .test()

        upstream.onComplete()

        assertSame(error, caughtException.value)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_downstream_disposed_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        val observer =
            upstream
                .doOnAfterFinally { throw error }
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
                .doOnAfterFinally { throw error }
                .test()

        observer.dispose()

        observer.assertDisposed()
    }

    @Test
    fun calls_uncaught_exception_handler_with_CompositeException_WHEN_upstream_produced_error_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error1 = Exception()
        val error2 = Exception()

        upstream
            .doOnAfterFinally { throw error2 }
            .test()

        upstream.onError(error1)

        val error: Throwable? = caughtException.value
        assertTrue(error is CompositeException)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }
}
