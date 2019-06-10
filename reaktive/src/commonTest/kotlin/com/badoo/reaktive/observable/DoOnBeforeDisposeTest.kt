package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.observable.TestObservable
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeDisposeTest
    : ObservableToObservableTests by ObservableToObservableTests<Unit>({ doOnBeforeDispose {} }) {

    private val upstream = TestObservable<Int>()

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = SafeMutableList<String>()

        observableUnsafe<Nothing> { observer ->
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
    fun does_not_call_action_WHEN_emitted_value() {
        val isCalled = AtomicReference(false)

        upstream
            .doOnBeforeDispose {
                isCalled.value = true
            }
            .test()

        upstream.onNext(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_completed() {
        val isCalled = AtomicReference(false)

        upstream
            .doOnBeforeDispose {
                isCalled.value = true
            }
            .test()

        upstream.onComplete()

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicReference(false)

        upstream
            .doOnBeforeDispose {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }
}