package com.badoo.reaktive.single

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSuccessTest
    : SingleToSingleTests by SingleToSingleTestsImpl({ doOnBeforeSuccess {} }) {

    private val upstream = TestSingle<Int>()

    @Test
    fun calls_action_before_emitting() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnBeforeSuccess { value ->
                callOrder += "action $value"
            }
            .subscribe(
                object : DefaultSingleObserver<Int> {
                    override fun onSuccess(value: Int) {
                        callOrder += "onNext $value"
                    }
                }
            )

        upstream.onSuccess(0)

        assertEquals(listOf("action 0", "onNext 0"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        var isCalled = false

        upstream
            .doOnBeforeSuccess { isCalled = true }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled)
    }

    @Test
    fun produces_error_WHEN_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeSuccess { throw error }
                .test()

        upstream.onSuccess(0)

        observer.assertError(error)
    }
}
