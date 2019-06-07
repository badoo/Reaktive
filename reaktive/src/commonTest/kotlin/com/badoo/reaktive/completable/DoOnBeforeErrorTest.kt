package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeErrorTest
    : CompletableToCompletableTests by CompletableToCompletableTests({ doOnBeforeError {} }) {

    @Test
    fun calls_action_before_failing() {
        val upstream = TestCompletable()
        val callOrder = SafeMutableList<Pair<String, Throwable>>()
        val exception = Exception()

        upstream
            .doOnBeforeError { error ->
                callOrder += "action" to error
            }
            .subscribe(
                object : DefaultCompletableObserver {
                    override fun onError(error: Throwable) {
                        callOrder += "onError" to error
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action" to exception, "onError" to exception), callOrder.items)
    }
}