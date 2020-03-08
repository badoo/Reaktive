package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.resetReaktiveUncaughtErrorHandler
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class DoOnAfterDisposeTest : SingleToSingleTests by SingleToSingleTestsImpl({ doOnAfterDispose {} }) {

    private val upstream = TestSingle<Int>()

    @AfterTest
    fun after() {
        resetReaktiveUncaughtErrorHandler()
    }

    @Test
    fun calls_action_after_disposing_upstream() {
        val callOrder = SharedList<String>()

        singleUnsafe<Nothing> { observer ->
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
        val isCalled = AtomicBoolean()

        singleUnsafe<Nothing> {}
            .doOnAfterDispose { isCalled.value = true }
            .test()
            .dispose()

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_succeeded() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnAfterDispose {
                isCalled.value = true
            }
            .test()

        upstream.onSuccess(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnAfterDispose {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
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
