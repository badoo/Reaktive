package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeCompleteTest
    : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ doOnBeforeComplete {} }) {

    private val upstream = TestMaybe<Int>()

    @Test
    fun calls_action_before_completion() {
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeComplete {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("action", "onComplete"), callOrder.items)
    }

    @Test
    fun does_not_call_action_WHEN_succeeded() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeComplete {
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
            .doOnBeforeComplete {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }
}