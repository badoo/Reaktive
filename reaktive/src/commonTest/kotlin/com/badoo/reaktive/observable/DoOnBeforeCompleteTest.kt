package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeCompleteTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ doOnBeforeComplete {} }) {

    @Test
    fun calls_action_before_completion() {
        val upstream = TestObservable<Nothing>()
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeComplete {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Nothing> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("action", "onComplete"), callOrder.items)
    }
}