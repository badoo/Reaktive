package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.test.base.assertDisposed
import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.TestMaybe
import com.badoo.reaktive.test.maybe.test
import com.badoo.reaktive.utils.SharedList
import com.badoo.reaktive.utils.atomic.AtomicBoolean
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DoOnBeforeDisposeTest
    : MaybeToMaybeTests by MaybeToMaybeTests<Unit>({ doOnBeforeDispose {} }) {

    private val upstream = TestMaybe<Int>()

    @Test
    fun calls_action_before_disposing_upstream() {
        val callOrder = SharedList<String>()

        maybeUnsafe<Nothing> { observer ->
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

        assertEquals(listOf("action", "dispose"), callOrder)
    }

    @Test
    fun does_not_call_action_WHEN_succeeded() {
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeDispose {
                isCalled.value = true
            }
            .test()

        upstream.onSuccess(0)

        assertFalse(isCalled.value)
    }

    @Test
    fun does_not_call_action_WHEN_completed() {
        val isCalled = AtomicBoolean()

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
        val isCalled = AtomicBoolean()

        upstream
            .doOnBeforeDispose {
                isCalled.value = true
            }
            .test()

        upstream.onError(Throwable())

        assertFalse(isCalled.value)
    }

    @Test
    fun produces_error_WHEN_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeDispose { throw error }
                .test()

        observer.dispose()

        observer.assertError(error)
    }

    @Test
    fun disposes_upstream_WHEN_exception_in_lambda() {
        val error = Exception()

        val observer =
            upstream
                .doOnBeforeDispose { throw error }
                .test()

        observer.dispose()

        observer.assertDisposed()
    }
}