package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class DoOnAfterDisposeTest : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ doOnAfterDispose {} }) {

    private val upstream = TestCompletable()

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
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
            .doOnAfterDispose {
                callOrder += "action"
            }
            .test()
            .dispose()

        assertEquals(listOf("dispose", "action"), callOrder)
    }

    @Test
    fun does_not_calls_action_WHEN_disposed_before_upstream_onSubscribe() {
        var isCalled = false

        completableUnsafe {}
            .doOnAfterDispose { isCalled = true }
            .test()
            .dispose()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_completed() {
        var isCalled = false

        upstream
            .doOnAfterDispose { isCalled = true }
            .test()

        upstream.onComplete()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        var isCalled = false

        upstream
            .doOnAfterDispose { isCalled = true }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_downstream_disposed_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        val observer =
            upstream
                .doOnAfterDispose { throw error }
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
                .doOnAfterDispose { throw error }
                .test()

        observer.dispose()

        observer.assertDisposed()
    }
}
