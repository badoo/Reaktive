package com.badoo.reaktive.single

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoOnAfterErrorTest : SingleToSingleTests by SingleToSingleTestsImpl({ doOnAfterError {} }) {

    private val upstream = TestSingle<Int>()

    @Test
    fun calls_action_after_failing() {
        val callOrder = ArrayList<Pair<String, Throwable>>()
        val exception = Throwable()

        upstream
            .doOnAfterError { error ->
                callOrder += "action" to error
            }
            .subscribe(
                object : DefaultSingleObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError" to error
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("onError" to exception, "action" to exception), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_succeeded() {
        var isCalled = false

        upstream
            .doOnAfterError { isCalled = true }
            .test()

        upstream.onSuccess(0)

        assertFalse(isCalled)
    }

    @Test
    fun calls_uncaught_exception_handler_with_CompositeException_WHEN_upstream_produced_error_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error1 = Exception()
        val error2 = Exception()

        upstream
            .doOnAfterError { throw error2 }
            .test()

        upstream.onError(error1)

        val error: Throwable? = caughtException.value
        assertTrue(error is CompositeException)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }
}
