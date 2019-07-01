package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeErrorTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ doOnBeforeError {} }) {

    private val upstream = TestObservable<Int>()

    @Test
    fun calls_action_before_failing() {
        val callOrder = SafeMutableList<Pair<String, Throwable>>()
        val exception = Exception()

        upstream
            .doOnBeforeError { error ->
                callOrder += "action" to error
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError" to error
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action" to exception, "onError" to exception), callOrder.items)
    }

    @Test
    fun does_not_call_action_WHEN_emitted_value() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeError {
                isCalled.value = true
            }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_completed() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeError {
                isCalled.value = true
            }
            .test()

        upstream.onComplete()

        assertFalse(isCalled.value)
    }
}