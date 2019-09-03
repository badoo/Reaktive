package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.observable.test
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.isActive
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine
import kotlin.test.Test
import kotlin.test.assertFalse

class SingleFromCoroutineTest {

    private val channel = BroadcastChannel<Int?>(Channel.BUFFERED)
    private val observer = channel.asFlow().asObservable().test()

    @Test
    fun succeeds_WHEN_coroutine_returned_non_null_value() {
        val observer = singleFromCoroutine { 0 }.test()

        observer.assertSuccess(0)
    }

    @Test
    fun succeeds_WHEN_coroutine_returned_null_value() {
        val observer = singleFromCoroutine<Unit?> { null }.test()

        observer.assertSuccess(null)
    }

    @Test
    fun produces_error_WHEN_coroutine_thrown_exception() {
        val error = Exception()

        val observer = singleFromCoroutine { throw error }.test()

        observer.assertError(error)
    }

    @Test
    fun cancels_flow_WHEN_disposable_is_disposed() {
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