package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.single.assertSuccess
import com.badoo.reaktive.test.single.test
import kotlin.test.Test

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
}
