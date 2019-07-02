package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import com.badoo.reaktive.utils.atomic.atomicList
import com.badoo.reaktive.utils.atomic.plusAssign
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSubscribeTest
    : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ doOnBeforeSubscribe {} }) {

    @Test
    fun calls_action_before_downstream_onSubscribe_WHEN_action_does_not_throw_exception() {
        val callOrder = atomicList<String>()

        maybeUnsafe<Nothing> {}
            .doOnBeforeSubscribe {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Nothing> {
                    override fun onSubscribe(disposable: Disposable) {
                        callOrder += "onSubscribe"
                    }
                }
            )

        assertEquals(listOf("action", "onSubscribe"), callOrder.value)
    }

    @Test
    fun delegates_error_to_downstream_after_downstream_onSubscribe_WHEN_action_throws_exception() {
        val callOrder = atomicList<Any>()
        val exception = Exception()

        maybeUnsafe<Nothing> {}
            .doOnBeforeSubscribe {
                throw exception
            }
            .subscribe(
                object : DefaultMaybeObserver<Nothing> {
                    override fun onSubscribe(disposable: Disposable) {
                        callOrder += "onSubscribe"
                    }

                    override fun onError(error: Throwable) {
                        callOrder += error
                    }
                }
            )

        assertEquals(listOf("onSubscribe", exception), callOrder.value)
    }

    @Test
    fun does_not_call_action_WHEN_onSubscribe_received_from_upstream() {
        val isCalled = AtomicBoolean()

        maybeUnsafe<Nothing> { observer ->
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
        val isCalled = AtomicBoolean()
        val upstream = TestMaybe<Int>()

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
    fun does_not_call_action_WHEN_completed() {
        val isCalled = AtomicBoolean()
        val upstream = TestMaybe<Nothing>()

        upstream
            .doOnBeforeSubscribe {
                isCalled.value = true
            }
            .test()

        isCalled.value = false
        upstream.onComplete()

        assertFalse(isCalled.value)
    }


    @Test
    fun does_not_call_action_WHEN_produced_error() {
        val isCalled = AtomicBoolean()
        val upstream = TestMaybe<Nothing>()

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