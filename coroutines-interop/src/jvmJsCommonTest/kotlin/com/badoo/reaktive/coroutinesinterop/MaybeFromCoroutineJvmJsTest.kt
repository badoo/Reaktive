package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.maybe.test
import kotlinx.coroutines.isActive
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertFalse

class MaybeFromCoroutineJvmJsTest {

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