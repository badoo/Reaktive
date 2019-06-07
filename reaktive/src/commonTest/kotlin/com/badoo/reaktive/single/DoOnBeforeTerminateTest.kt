package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeTerminateTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeTerminate {} }) {

    private val upstream = TestSingle<Int>()

    @Test
    fun calls_action_before_success() {
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultSingleObserver<Int> {
                    override fun onSuccess(value: Int) {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onSuccess(0)

        assertEquals(listOf("action", "onComplete"), callOrder.items)
    }

    @Test
    fun calls_action_before_failing() {
        val callOrder = SafeMutableList<String>()
        val exception = Exception()

        upstream
            .doOnBeforeTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultSingleObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action", "onError"), callOrder.items)
    }
}