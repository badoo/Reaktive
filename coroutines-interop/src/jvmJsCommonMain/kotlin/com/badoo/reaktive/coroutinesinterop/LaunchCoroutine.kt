package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal actual inline fun <T> launchCoroutine(
    setDisposable: (Disposable) -> Unit,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend CoroutineScope.() -> T
) {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }

    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope
        .launch(Dispatchers.Unconfined + exceptionHandler) {
            try {
                onSuccess(block())
            } catch (ignored: CancellationException) {
            }
        }
        .asDisposable()
        .also(setDisposable)
}
