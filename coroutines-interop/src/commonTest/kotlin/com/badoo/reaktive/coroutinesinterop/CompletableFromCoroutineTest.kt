package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.test.base.assertError
import com.badoo.reaktive.test.completable.assertComplete
import com.badoo.reaktive.test.completable.test
import kotlin.test.Test

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
}