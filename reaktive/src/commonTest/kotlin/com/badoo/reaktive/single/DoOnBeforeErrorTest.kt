package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeErrorTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeError {} }) {

    private val upstream = TestSingle<Int>()

    @Test
    fun calls_action_before_failing() {
        val callOrder = SafeMutableList<Pair<String, Throwable>>()
        val exception = Exception()

        upstream
            .doOnBeforeError { error ->
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

        assertEquals(listOf("action" to exception, "onError" to exception), callOrder.items)
    }

    @Test
    fun does_not_call_action_WHEN_succeeded_value() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeError {
                isCalled.value = true
            }
            .test()

        upstream.onSuccess(0)

        assertFalse(isCalled.value)
    }
}