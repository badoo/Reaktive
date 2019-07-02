package com.badoo.reaktive.observable

import com.badoo.reaktive.test.observable.DefaultObservableObserver
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeTerminateTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ doOnBeforeTerminate {} }) {

    private val upstream = TestObservable<Int>()
    private val callOrder = atomicList<String>()

    @Test
    fun calls_action_before_completion() {
        upstream
            .doOnBeforeTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("action", "onComplete"), callOrder.value)
    }

    @Test
    fun calls_action_before_failing() {
        val callOrder = atomicList<String>()
        val exception = Exception()

        upstream
            .doOnBeforeTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultObservableObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action", "onError"), callOrder.value)
    }

    @Test
    fun does_not_call_action_WHEN_emitted_value() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeFinally {
                isCalled.value = true
            }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled.value)
    }
}