package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.blockingGet

actual fun <T> Single<T>.testAwait(assertError: ((Throwable) -> Unit)?, assertSuccess: (T) -> Unit) {
    if (assertError == null) {
        assertSuccess(blockingGet())
    } else {
        val result =
            try {
                blockingGet()
            } catch (e: Throwable) {
                assertError(e)
                return
            }

        assertSuccess(result)
    }
}
