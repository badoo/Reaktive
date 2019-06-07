package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeDisposeTest
    : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ doOnBeforeDispose {} }) {

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = SafeMutableList<String>()

        maybeUnsafe<Nothing> { observer ->
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