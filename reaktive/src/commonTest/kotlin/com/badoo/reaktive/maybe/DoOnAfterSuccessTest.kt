package com.badoo.reaktive.maybe

import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class DoOnAfterSuccessTest : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ doOnAfterSuccess {} }) {

    private val upstream = TestMaybe<Int>()

    @Test
    fun calls_action_after_success() {
        val callOrder = ArrayList<String>()

        upstream
            .doOnAfterSuccess {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
                    override fun onSuccess(value: Int) {
                        callOrder += "onSuccess"
                    }
                }
            )

        upstream.onSuccess(0)

        assertEquals(listOf("onSuccess", "action"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_completed() {
        var isCalled = false

        upstream
            .doOnAfterSuccess { isCalled = true }
            .test()

        upstream.onComplete()

        assertFalse(isCalled)
    }

    @Test
    fun does_not_call_action_WHEN_upstream_produced_error() {
        var isCalled = false

        upstream
            .doOnAfterSuccess { isCalled = true }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled)
    }

    @Test
    fun calls_uncaught_exception_handler_WHEN_upstream_succeeded_and_exception_in_lambda() {
        val caughtException = mockUncaughtExceptionHandler()
        val error = Exception()

        upstream
            .doOnAfterSuccess { throw error }
            .test()

        upstream.onSuccess(0)

        assertSame(error, caughtException.value)
    }
}
