package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSuccessTest
    : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ doOnBeforeSuccess {} }) {

    private val upstream = TestMaybe<Int>()

    @Test
    fun calls_action_before_emitting() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnBeforeSuccess { value ->
                callOrder += "action $value"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
                    override fun onSuccess(value: Int) {
                        callOrder += "onNext $value"
                    }
                }
            )

        upstream.onSuccess(0)

        assertEquals(listOf("action 0", "onNext 0"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_completed() {
        var isCalled = false

        upstream
            .doOnBeforeSuccess { isCalled = true }
            .test()

        upstream.onComplete()

        assertFalse(isCalled)
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
