package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.DefaultMaybeObserver
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.utils.SharedList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class DoOnBeforeTerminateTest
    : MaybeToMaybeTests by MaybeToMaybeTestsImpl({ doOnBeforeTerminate {} }) {

    private val upstream = TestMaybe<Int>()
    private val callOrder = SharedList<String>()

    @Test
    fun calls_action_before_completion() {

        upstream
            .doOnBeforeTerminate {
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

        assertEquals(listOf("action", "onComplete"), callOrder)
    }

    @Test
    fun calls_action_before_success() {
        val callOrder = SharedList<String>()

        upstream
            .doOnBeforeTerminate {
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

        assertEquals(listOf("action", "onSuccess"), callOrder)
    }

    @Test
    fun calls_action_before_failing() {
        val callOrder = SharedList<String>()
        val exception = Exception()

        upstream
            .doOnBeforeTerminate {
                callOrder += "action"
            }
            .subscribe(
                object : DefaultMaybeObserver<Int> {
                    override fun onError(error: Throwable) {
                        callOrder += "onError"
                    }
                }
            )

        upstream.onError(exception)

        assertEquals(listOf("action", "onError"), callOrder)
    }

    @Test
    fun produces_error_WHEN_upstream_completed_and_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeTerminate { throw error }
                .test()

        upstream.onComplete()

        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_upstream_succeeded_and_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeTerminate { throw error }
                .test()

        upstream.onSuccess(0)

        observer.assertError(error)
    }

    @Test
    fun produces_CompositeException_WHEN_upstream_produced_error_and_exception_in_lambda() {
        val error1 = Exception()
        val error2 = Exception()

        val observer =
            upstream
                .doOnBeforeTerminate { throw error2 }
                .test()

        upstream.onError(error1)

        val error: Throwable? = observer.error
        assertTrue(error is CompositeException)
        assertSame(error1, error.cause1)
        assertSame(error2, error.cause2)
    }
}
