package com.badoo.reaktive.test.completable

import com.badoo.reaktive.completable.completableFromFunction
import com.badoo.reaktive.completable.completableOfEmpty
import com.badoo.reaktive.completable.completableOfError
import com.badoo.reaktive.completable.onErrorResumeNext
import com.badoo.reaktive.completable.subscribeOn
import com.badoo.reaktive.scheduler.computationScheduler
import kotlin.test.Test
import kotlin.test.assertSame

class TestAwaitTest {

    @Test
    fun awaits_success() =
        completableOfEmpty()
            .subscribeOn(computationScheduler)
            .testAwait()

    @Test
    fun awaits_error() {
        val error = Exception()

        return completableOfError(error)
            .subscribeOn(computationScheduler)
            .onErrorResumeNext {
                completableFromFunction {
                    assertSame(error, it)
                }
            }
            .testAwait()
    }
}
