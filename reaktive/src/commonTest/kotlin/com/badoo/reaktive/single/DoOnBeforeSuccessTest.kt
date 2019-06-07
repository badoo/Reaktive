package com.badoo.reaktive.single

import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeSuccessTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeSuccess {} }) {

    @Test
    fun calls_action_before_emitting() {
        val upstream = TestSingle<Int>()
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
}