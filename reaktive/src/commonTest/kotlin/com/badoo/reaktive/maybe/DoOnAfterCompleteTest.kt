package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class DoOnAfterCompleteTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ doOnAfterComplete {} }) {

    private val upstream = TestMaybe<Int>()

    @Test
    fun does_not_call_action_WHEN_upstream_succeeded() {
        var isCalled = false

        upstream
            .doOnAfterComplete { isCalled = true }
            .test()

        upstream.onSuccess(0)

        assertFalse(isCalled)
    }

    @Test
    fun calls_action_after_completion() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnAfterComplete {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
                    override fun onComplete() {
                        callOrder += "onComplete"
                    }
                }
            )

        upstream.onComplete()

        assertEquals(listOf("onComplete", "action"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        var isCalled = false

        upstream
            .doOnAfterComplete { isCalled = true }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_upstream_completed_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        upstream
            .doOnAfterComplete { throw error }
            .test()

        upstream.onComplete()

        assertSame(error, caughtException.value)
    }
}
