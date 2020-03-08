package com.badoo.reaktive.completable

import com.badoo.reaktive.test.completable.DefaultCompletableObserver
import com.badoo.reaktive.test.completable.TestCompletable
import com.badoo.reaktive.test.completable.test
import com.badoo.reaktive.test.mockUncaughtExceptionHandler
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame

class DoOnAfterCompleteTest : CompletableToCompletableTests by CompletableToCompletableTestsImpl({ doOnAfterComplete {} }) {

    private val upstream = TestCompletable()

    @Test
    fun calls_action_after_completion() {
        val callOrder = SharedList<String>()

        upstream
            .doOnAfterComplete {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultCompletableObserver {
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
        val isCalled = AtomicBoolean()

        upstream
            .doOnAfterComplete {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
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
