package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.single.test
import kotlinx.coroutines.isActive
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertFalse

class SingleFromCoroutineJvmJsTest {

    @Test
    fun cancels_coroutine_WHEN_disposable_is_disposed() {
        lateinit var continuation: Continuation<Nothing>

        val observer =
            singleFromCoroutine {
                suspendCoroutine<Unit> {
                    continuation = it
                }
            }
                .test()

        observer.dispose()

        assertFalse(continuation.context.isActive)
    }
}