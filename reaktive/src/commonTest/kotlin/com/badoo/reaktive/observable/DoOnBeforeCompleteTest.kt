package com.badoo.reaktive.observable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeCompleteTest :
    ObservableToObservableTests by ObservableToObservableTestsImpl({ doOnBeforeComplete {} }),
    ObservableToObservableForwardTests by ObservableToObservableForwardTestsImpl({ doOnBeforeComplete {} }) {

    private val upstream = TestObservable<Int>()

    @Test
    fun calls_action_before_completion() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnBeforeComplete {
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
    fun does_not_call_action_WHEN_upstream_emitted_value() {
        var isCalled = false

        upstream
            .doOnBeforeComplete { isCalled = true }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        var isCalled = false

        upstream
            .doOnBeforeComplete { isCalled = true }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled)
    }

    @Test
    fun produces_error_WHEN_upstream_completed_and_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeComplete { throw error }
                .test()

        upstream.onComplete()

        observer.assertError(error)
    }
}
