package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeDisposeTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeDispose {} }) {

    private val upstream = TestSingle<Int>()

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

    @Test
    fun does_not_call_action_WHEN_succeeded() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeDispose {
                isCalled.value = true
            }
            .test()

        upstream.onSuccess(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeDispose {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }
}