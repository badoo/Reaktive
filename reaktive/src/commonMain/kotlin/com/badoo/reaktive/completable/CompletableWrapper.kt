package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.completable.subscribe as subscribeRx

/**
 * Wrappers are normally exposed to Swift.
 * You can also extend the wrapper class if you need to expose any additional operators.
 */
open class CompletableWrapper(inner: Completable) : Completable by inner {

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onSubscribe = onSubscribe,
            onError = onError,
            onComplete = onComplete
        )

    @UseReturnValue
    fun subscribe(): Disposable = subscribeRx()

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onComplete: () -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onComplete = onComplete
        )

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onError = onError,
            onComplete = onComplete
        )
}

fun Completable.wrap(): CompletableWrapper = CompletableWrapper(this)
