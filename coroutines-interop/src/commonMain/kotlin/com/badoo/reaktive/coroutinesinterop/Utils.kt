package com.badoo.reaktive.coroutinesinterop

import com.badoo.reaktive.disposable.Disposable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal inline fun <T> launchCoroutine(
    context: CoroutineContext = Dispatchers.Unconfined,
    crossinline onSuccess: (T) -> Unit,
    crossinline onError: (Throwable) -> Unit,
    crossinline block: suspend () -> T
): Disposable =
    GlobalScope
        .launch(context) {
            try {
                block()
            } catch (e: Throwable) {
                onError(e)
                return@launch
            }
                .also(onSuccess)
        }
        .asDisposable()