package com.badoo.reaktive.test.single

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.doOnBeforeSuccess
import com.badoo.reaktive.single.map
import com.badoo.reaktive.single.onErrorReturn
import com.badoo.reaktive.single.subscribe

actual fun <T> Single<T>.testAwait(assertError: ((Throwable) -> Unit)?, assertSuccess: (T) -> Unit): AsyncTestResult =
    if (assertError == null) {
        doOnBeforeSuccess(assertSuccess)
            .asTestResult()
    } else {
        map { TestAwaitResult.Success(it) }
            .onErrorReturn { TestAwaitResult.Error(it) }
            .doOnBeforeSuccess { result ->
                when (result) {
                    is TestAwaitResult.Success -> assertSuccess(result.value)
                    is TestAwaitResult.Error -> assertError(result.error)
                }
            }
            .asTestResult()
    }

private fun <T> Single<T>.asTestResult(): AsyncTestResult =
    AsyncTestResult { resolve, reject ->
        subscribe(
            onSuccess = { resolve(Unit.toJsReference()) },
            onError = { reject(it.toJsReference()) },
        )
    }

private sealed class TestAwaitResult<out T> {
    class Success<out T>(val value: T) : TestAwaitResult<T>()
    class Error(val error: Throwable) : TestAwaitResult<Nothing>()
}
