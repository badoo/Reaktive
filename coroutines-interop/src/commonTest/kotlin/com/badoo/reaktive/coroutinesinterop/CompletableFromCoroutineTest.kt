package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
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

class CompletableFromCoroutineTest {

    @Test
    fun completes_WHEN_coroutine_finished() {
        val observer = completableFromCoroutine {}.test()

        observer.assertComplete()
    }

    @Test
    fun produces_error_WHEN_coroutine_thrown_exception() {
        val error = Exception()

        val observer = completableFromCoroutine { throw error }.test()

        observer.assertError(error)
    }

    @Test
    fun produces_error_WHEN_launch_in_coroutine_thrown_exception() {
        val observer =
            completableFromCoroutine {
                launch { throw Exception("Msg") }
                yield()
            }.test()

        assertEquals("Msg", observer.error?.message)
    }

    @Test
    fun cancels_coroutine_WHEN_disposable_is_disposed() {
        var scope by AtomicReference<CoroutineScope?>(null)
        val observer = completableFromCoroutine { scope = this }.test()

        observer.dispose()

        assertFalse(scope!!.isActive)
    }
}
