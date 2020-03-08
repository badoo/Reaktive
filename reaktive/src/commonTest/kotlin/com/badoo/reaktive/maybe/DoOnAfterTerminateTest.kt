package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.utils.SharedList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoOnAfterTerminateTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ doOnAfterTerminate {} }) {

    private val upstream = TestMaybe<Int>()
    private val callOrder = SharedList<String>()

    @Test
    fun calls_action_after_success() {
        upstream
            .doOnAfterTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
                    override fun onSuccess(value: Int) {
                        callOrder += "onSuccess"
                    }
                }
            )

        upstream.onSuccess(0)

        assertEquals(listOf("onSuccess", "action"), callOrder)
    }

    @Test
    fun calls_action_after_completion() {
        upstream
            .doOnAfterTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
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
        val callOrder = SharedList<String>()
        val exception = Exception()

        upstream
            .doOnAfterTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("onError", "action"), callOrder)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_upstream_succeeded_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        upstream
            .doOnAfterTerminate { throw error }
            .test()

        upstream.onSuccess(0)

        assertSame(error, caughtException.value)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_upstream_completed_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        upstream
            .doOnAfterTerminate { throw error }
            .test()

        upstream.onComplete()

        assertSame(error, caughtException.value)
    }

    @Test
    fun calls_uncaught_exception_handler_with_CompositeException_WHEN_upstream_produced_error_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error1 = Exception()
        val error2 = Exception()

        upstream
            .doOnAfterTerminate { throw error2 }
            .test()

        upstream.onError(error1)

        val error: Throwable? = caughtException.value
        assertTrue(error is CompositeException)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }
}
