package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeDisposeTest
    : CompletableToCompletableTests by CompletableToCompletableTests({ doOnBeforeDispose {} }) {

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = SafeMutableList<String>()

        completableUnsafe { observer ->
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