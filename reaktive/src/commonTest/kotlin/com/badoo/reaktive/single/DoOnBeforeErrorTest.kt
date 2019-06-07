package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeErrorTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeError {} }) {

    @Test
    fun calls_action_before_failing() {
        val upstream = TestSingle<Nothing>()
        val callOrder = SafeMutableList<Pair<String, Throwable>>()
        val exception = Exception()

        upstream
            .doOnBeforeError { error ->
                callOrder += "action" to error
            }
            .subscribe(
                object : DefaultSingleObserver<Nothing> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError" to error
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action" to exception, "onError" to exception), callOrder.items)
    }
}