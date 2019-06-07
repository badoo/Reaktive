package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeErrorTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ doOnBeforeError {} }) {

    @Test
    fun calls_action_before_failing() {
        val upstream = TestObservable<Nothing>()
        val callOrder = SafeMutableList<Pair<String, Throwable>>()
        val exception = Exception()

        upstream
            .doOnBeforeError { error ->
                callOrder += "action" to error
            }
            .subscribe(
                object : DefaultObservableObserver<Nothing> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError" to error
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action" to exception, "onError" to exception), callOrder.items)
    }
}