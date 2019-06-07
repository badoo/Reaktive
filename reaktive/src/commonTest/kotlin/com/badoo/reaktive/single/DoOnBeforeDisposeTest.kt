package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeDisposeTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeDispose {} }) {

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = SafeMutableList<String>()

        singleUnsafe<Nothing> { observer ->
            observer.onSubscribe(
                disposable {
                    callOrder += "dispose"
                }
            )
        }
            .doOnBeforeDispose {
                callOrder += "action"
            }
            .test()
            .dispose()

        assertEquals(listOf("action", "dispose"), callOrder.items)
    }
}