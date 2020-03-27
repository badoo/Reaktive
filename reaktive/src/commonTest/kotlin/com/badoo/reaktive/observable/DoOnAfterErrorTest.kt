package com.badoo.reaktive.observable

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoOnAfterErrorTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ doOnAfterError {} }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ doOnAfterError {} }) {

    private val upstream = TestObservable<Int>()

    @Test
    fun calls_action_after_failing() {
        val callOrder = SharedList<Pair<String, Throwable>>()
        val exception = Throwable()

        upstream
            .doOnAfterError { error ->
                callOrder += "action" to error
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError" to error
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("onError" to exception, "action" to exception), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_emitted_value() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnAfterError {
                isCalled.value = true
            }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_completed() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnAfterError {
                isCalled.value = true
            }
            .test()

        upstream.onComplete()

        assertFalse(isCalled.value)
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
