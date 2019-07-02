package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeTerminateTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeTerminate {} }) {

    private val upstream = TestSingle<Int>()

    @Test
    fun calls_action_before_success() {
        val callOrder = atomicList<String>()

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

        assertEquals(listOf("action", "onComplete"), callOrder.value)
    }

    @Test
    fun calls_action_before_failing() {
        val callOrder = atomicList<String>()
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

        assertEquals(listOf("action", "onError"), callOrder.value)
    }
}