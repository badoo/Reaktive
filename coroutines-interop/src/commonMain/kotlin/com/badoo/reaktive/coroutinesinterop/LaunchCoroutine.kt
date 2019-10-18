package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import kotlinx.coroutines.CoroutineScope

internal expect inline fun <T> launchCoroutine(
    setDisposable: (Disposable) -> Unit,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend CoroutineScope.() -> T
)
