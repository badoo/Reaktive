package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.disposable
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal actual inline fun <T> launchCoroutine(
    setDisposable: (Disposable) -> Unit,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend CoroutineScope.() -> T
) {
    val disposable = disposable()
    setDisposable(disposable)

    try {
        // Event loop is required
        runBlocking {
            if (disposable.isDisposed) {
                return@runBlocking
            }

            launchWatchdog(disposable)
            onSuccess(block())
        }
    } catch (ignored: CancellationException) {
    } catch (e: Throwable) {
        onError(e)
    }
}

/*
 * It's not allowed to freeze a Job nor Scope, looks like the only way
 * to cancel a coroutine is to check for a condition periodically
 */
private fun CoroutineScope.launchWatchdog(disposable: Disposable) {
    launch {
        while (!disposable.isDisposed) {
            delay(COROUTINE_DELAY_CHECK_MS)
        }
        this@launchWatchdog.cancel()
    }
}

private const val COROUTINE_DELAY_CHECK_MS = 100L
