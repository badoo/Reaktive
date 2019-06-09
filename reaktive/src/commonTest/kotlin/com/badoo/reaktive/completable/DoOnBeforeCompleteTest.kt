package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeCompleteTest
    : CompletableToCompletableTests by CompletableToCompletableTests({ doOnBeforeComplete {} }) {

    private val upstream = TestCompletable()

    @Test
    fun calls_action_before_completion() {
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeComplete {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultCompletableObserver {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("action", "onComplete"), callOrder.items)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicReference(false)

        upstream
            .doOnBeforeComplete {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }
}