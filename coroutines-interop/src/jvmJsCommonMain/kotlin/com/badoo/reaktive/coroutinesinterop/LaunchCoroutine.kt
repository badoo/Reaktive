package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

internal actual inline fun <T> launchCoroutine(
    setDisposable: (Disposable) -> Unit,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend CoroutineScope.() -> T
) {
    GlobalScope
        .launch(Dispatchers.Unconfined) {
            try {
                onSuccess(block())
            } catch (ignored: CancellationException) {
            } catch (e: Throwable) {
                onError(e)
            }
        }
        .asDisposable()
        .also(setDisposable)
}
