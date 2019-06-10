package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSuccessTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeSuccess {} }) {

    private val upstream = TestSingle<Int>()

    @Test
    fun calls_action_before_emitting() {
        val callOrder = SafeMutableList<String>()

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

        assertEquals(listOf("action 0", "onNext 0"), callOrder.items)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicReference(false)

        upstream
            .doOnBeforeSuccess {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }
}