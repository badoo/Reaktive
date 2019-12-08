package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSuccessTest
    : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ doOnBeforeSuccess {} }) {

    private val upstream = TestMaybe<Int>()

    @Test
    fun calls_action_before_emitting() {
        val callOrder = SharedList<String>()

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
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeSuccess {
                isCalled.value = true
            }
            .test()

        upstream.onComplete()

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeSuccess {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
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
