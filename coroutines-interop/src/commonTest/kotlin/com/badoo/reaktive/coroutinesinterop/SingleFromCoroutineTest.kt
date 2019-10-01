package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlinx.coroutines.isActive
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertFalse

class SingleFromCoroutineTest {

    @Test
    fun succeeds_WHEN_coroutine_returned_non_null_value() {
        val observer = singleFromCoroutine { 0 }.test(autoFreeze = false)

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_WHEN_coroutine_returned_null_value() {
        val observer = singleFromCoroutine<Unit?> { null }.test(autoFreeze = false)

        observer.assertSuccess(null)
    }

    @Test
    fun produces_error_WHEN_coroutine_thrown_exception() {
        val error = Exception()

        val observer = singleFromCoroutine { throw error }.test(autoFreeze = false)

        observer.assertError(error)
    }

    @Test
    fun cancels_coroutine_WHEN_disposable_is_disposed() {
        lateinit var continuation: Continuation<Nothing>

        val observer =
            singleFromCoroutine {
                suspendCoroutine<Unit> {
                    continuation = it
                }
            }
                .test(autoFreeze = false)

        observer.dispose()

        assertFalse(continuation.context.isActive)
    }
}