package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.single.DefaultSingleObserver
import com.badoo.reaktive.test.single.TestSingle
import com.badoo.reaktive.test.single.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnAfterSubscribeTest : SingleToSingleTests by SingleToSingleTestsImpl({ doOnAfterSubscribe {} }) {

    @Test
    fun calls_action_after_downstream_onSubscribe_WHEN_action_does_not_throw_exception() {
        val callOrder = ArrayList<String>()

        singleUnsafe<Nothing> {}
            .doOnAfterSubscribe {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultSingleObserver<Nothing> {
                    override fun onSubscribe(disposable: Disposable) {
                        callOrder += "onSubscribe"
                    }
                }
            )

        assertEquals(listOf("onSubscribe", "action"), callOrder)
    }

    @Test
    fun delegates_error_to_downstream_after_downstream_onSubscribe_WHEN_action_throws_exception() {
        val callOrder = ArrayList<Any>()
        val exception = Exception()

        singleUnsafe<Nothing> {}
            .doOnAfterSubscribe {
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
                .doOnAfterSubscribe { throw Exception() }
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
            .doOnAfterSubscribe { isCalled = true }
            .test()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_succeeded() {
        var isCalled: Boolean
        val upstream = TestSingle<Int>()

        upstream
            .doOnAfterSubscribe { isCalled = true }
            .test()

        isCalled = false
        upstream.onSuccess(0)

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        var isCalled: Boolean
        val upstream = TestSingle<Nothing>()

        upstream
            .doOnAfterSubscribe { isCalled = true }
            .test()

        isCalled = false
        upstream.onError(Throwable())

        assertFalse(isCalled)
    }
}
