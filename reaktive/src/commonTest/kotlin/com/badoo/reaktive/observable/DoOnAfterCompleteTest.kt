package com.badoo.reaktive.observable

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

class DoOnAfterCompleteTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ doOnAfterComplete {} }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ doOnAfterComplete {} }) {

    private val upstream = TestObservable<Int>()

    @Test
    fun calls_action_after_completion() {
        val callOrder = SharedList<String>()

        upstream
            .doOnAfterComplete {
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

        assertEquals(listOf("onComplete", "action"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_emitted_value() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnAfterComplete {
                isCalled.value = true
            }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnAfterComplete {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_upstream_completed_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        upstream
            .doOnAfterComplete { throw error }
            .test()

        upstream.onComplete()

        assertSame(error, caughtException.value)
    }
}
