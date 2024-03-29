package com.badoo.reaktive.completable

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeCompleteTest
    : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ doOnBeforeComplete {} }) {

    private val upstream = TestCompletable()

    @Test
    fun calls_action_before_completion() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnBeforeComplete {
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

        assertEquals(listOf("action", "onComplete"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        var isCalled = false

        upstream
            .doOnBeforeComplete { isCalled = true }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled)
    }

    @Test
    fun produces_error_WHEN_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeComplete { throw error }
                .test()

        upstream.onComplete()

        observer.assertError(error)
    }
}
