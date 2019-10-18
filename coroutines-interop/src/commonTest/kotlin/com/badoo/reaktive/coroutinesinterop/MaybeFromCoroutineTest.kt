package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.maybe.assertSuccess
import com.badoo.reaktive.test.maybe.test
import kotlin.test.Test

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
}
