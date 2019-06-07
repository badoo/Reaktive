package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeCompleteTest
    : CompletableToCompletableTests by CompletableToCompletableTests({ doOnBeforeComplete {} }) {

    @Test
    fun calls_action_before_completion() {
        val upstream = TestCompletable()
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
}