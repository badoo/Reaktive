package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import kotlinx.coroutines.isActive
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertFalse

class MaybeFromCoroutineTest {

    @Test
    fun succeeds_WHEN_coroutine_returned_non_null_value() {
        val observer = maybeFromCoroutine { 0 }.test()

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_WHEN_coroutine_returned_null_value() {
        val observer = maybeFromCoroutine<Unit?> { null }.test()

        observer.assertSuccess(null)
    }

    @Test
    fun produces_error_WHEN_coroutine_thrown_exception() {
        val error = Exception()

        val observer = maybeFromCoroutine { throw error }.test()

        observer.assertError(error)
    }

    @Test
    fun cancels_coroutine_WHEN_disposable_is_disposed() {
        lateinit var continuation: Continuation<Nothing>

        val observer =
            maybeFromCoroutine {
                suspendCoroutine<Unit> {
                    continuation = it
                }
            }
                .test()

        observer.dispose()

        assertFalse(continuation.context.isActive)
    }
}