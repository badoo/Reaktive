package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.utils.SafeMutableList
import kotlin.test.Test
import kotlin.test.assertEquals

class DoOnBeforeNextTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ doOnBeforeNext {} }) {

    @Test
    fun calls_action_before_emitting() {
        val upstream = TestObservable<Int>()
        val callOrder = SafeMutableList<String>()

        upstream
            .doOnBeforeNext { value ->
                callOrder += "action $value"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onNext(value: Int) {
                        callOrder += "onNext $value"
                    }
                }
            )

        upstream.onNext(0)
        upstream.onNext(1)

        assertEquals(listOf("action 0", "onNext 0", "action 1", "onNext 1"), callOrder.items)
    }
}