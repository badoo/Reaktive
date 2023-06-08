package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
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
    fun produces_error_WHEN_launch_in_coroutine_thrown_exception() {
        val observer =
            maybeFromCoroutine {
                launch { throw Exception("Msg") }
                yield()
            }.test()

        assertEquals("Msg", observer.error?.message)
    }

    @Test
    fun cancels_coroutine_WHEN_disposable_is_disposed() {
        var scope: CoroutineScope? = null
        val observer = maybeFromCoroutine { scope = this }.test()

        observer.dispose()

        assertFalse(scope!!.isActive)
    }
}
