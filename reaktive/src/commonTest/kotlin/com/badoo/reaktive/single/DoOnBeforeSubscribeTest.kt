package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeSubscribeTest
    : SingleToSingleTests by SingleToSingleTestsImpl({ doOnBeforeSubscribe {} }) {

    @Test
    fun calls_action_before_downstream_onSubscribe_WHEN_action_does_not_throw_exception() {
        val callOrder = ArrayList<String>()

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

        assertEquals(listOf("action", "onSubscribe"), callOrder)
    }

    @Test
    fun delegates_error_to_downstream_after_downstream_onSubscribe_WHEN_action_throws_exception() {
        val callOrder = ArrayList<Any>()
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

        assertEquals(listOf<Any>("onSubscribe", exception), callOrder)
    }

    @Test
    fun disposes_downstream_disposable_WHEN_action_throws_exception() {
        val observer =
            singleUnsafe<Nothing> {}
                .doOnBeforeSubscribe { throw Exception() }
                .test()

        observer.assertDisposed()
    }

    @Test
    fun does_not_call_action_WHEN_onSubscribe_received_from_upstream() {
        var isCalled = false

        singleUnsafe<Nothing> { observer ->
            isCalled = false
            observer.onSubscribe(Disposable())
        }
            .doOnBeforeSubscribe { isCalled = true }
            .test()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_succeeded() {
        var isCalled: Boolean
        val upstream = TestSingle<Int>()

        upstream
            .doOnBeforeSubscribe { isCalled = true }
            .test()

        isCalled = false
        upstream.onSuccess(0)

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_produced_error() {
        var isCalled: Boolean
        val upstream = TestSingle<Nothing>()

        upstream
            .doOnBeforeSubscribe { isCalled = true }
            .test()

        isCalled = false
        upstream.onError(Throwable())

        assertFalse(isCalled)
    }
}
