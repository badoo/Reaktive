package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import com.badoo.reaktive.utils.atomic.AtomicReference
import com.badoo.reaktive.utils.atomic.getValue
import com.badoo.reaktive.utils.atomic.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class SingleFromCoroutineTest {

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
    fun produces_error_WHEN_launch_in_coroutine_thrown_exception() {
        val observer =
            singleFromCoroutine {
                launch { throw Exception("Msg") }
                yield()
            }.test()

        assertEquals("Msg", observer.error?.message)
    }

    @Test
    fun cancels_coroutine_WHEN_disposable_is_disposed() {
        var scope by AtomicReference<CoroutineScope?>(null)
        val observer = singleFromCoroutine { scope = this }.test()

        observer.dispose()

        assertFalse(scope!!.isActive)
    }
}
