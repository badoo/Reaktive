package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.asPromise
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.onErrorReturn

actual fun <T> Single<T>.testAwait(assertError: ((Throwable) -> Unit)?, assertSuccess: (T) -> Unit): dynamic =
    if (assertError == null) {
        doOnBeforeSuccess(assertSuccess)
            .asPromise()
    } else {
        map { TestAwaitResult.Success(it) }
            .onErrorReturn { TestAwaitResult.Error(it) }
            .doOnBeforeSuccess { result ->
                when (result) {
                    is TestAwaitResult.Success -> assertSuccess(result.value)
                    is TestAwaitResult.Error -> assertError(result.error)
                }
            }
            .asPromise()
    }

private sealed class TestAwaitResult<out T> {
    class Success<out T>(val value: T) : TestAwaitResult<T>()
    class Error(val error: Throwable) : TestAwaitResult<Nothing>()
}
