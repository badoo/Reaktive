package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.test.utils.SafeMutableList
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSubscribeTest
    : SingleToSingleTests by SingleToSingleTests<Unit>({ doOnBeforeSubscribe {} }) {

    @Test
    fun calls_action_before_downstream_onSubscribe_WHEN_action_does_not_throw_exception() {
        val callOrder = SafeMutableList<String>()

        singleUnsafe<Nothing> {}
            .doOnBeforeSubscribe {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultSingleObserver<Nothing> {
                    override fun onSubscribe(disposable: Disposable) {
                        callOrder += "onSubscribe"
                    }
                }
            )

        assertEquals(listOf("action", "onSubscribe"), callOrder.items)
    }

    @Test
    fun delegates_error_to_downstream_after_downstream_onSubscribe_WHEN_action_throws_exception() {
        val callOrder = SafeMutableList<Any>()
        val exception = Exception()

        singleUnsafe<Nothing> {}
            .doOnBeforeSubscribe {
                throw exception
            }
            .subscribe(
                object : DefaultSingleObserver<Nothing> {
                    override fun onSubscribe(disposable: Disposable) {
                        callOrder += "onSubscribe"
                    }

                    override fun onError(error: Throwable) {
                        callOrder += error
                    }
                }
            )

        assertEquals(listOf("onSubscribe", exception), callOrder.items)
    }

    @Test
    fun does_not_call_action_WHEN_onSubscribe_received_from_upstream() {
        val isCalled = AtomicReference(false)

        singleUnsafe<Nothing> { observer ->
            isCalled.value = false
            observer.onSubscribe(disposable())
        }
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_succeeded() {
        val isCalled = AtomicReference(false)
        val upstream = TestSingle<Int>()

        upstream
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        isCalled.value = false
        upstream.onSuccess(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicReference(false)
        val upstream = TestSingle<Nothing>()

        upstream
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        isCalled.value = false
        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }
}