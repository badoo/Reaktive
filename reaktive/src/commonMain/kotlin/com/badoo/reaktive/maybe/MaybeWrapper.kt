package com.badoo.reaktive.maybe

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.maybe.subscribe as subscribeRx

/**
 * Wrappers are normally exposed to Swift.
 * You can also extend the wrapper class if you need to expose any additional operators.
 */
open class MaybeWrapper<out T : Any>(inner: Maybe<T>) : Maybe<T> by inner {

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onSubscribe = onSubscribe,
            onError = onError,
            onComplete = onComplete,
            onSuccess = onSuccess
        )

    @UseReturnValue
    fun subscribe(): Disposable = subscribeRx()

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onSuccess: (T) -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onSuccess = onSuccess
        )

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onComplete: () -> Unit,
        onSuccess: (T) -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onComplete = onComplete,
            onSuccess = onSuccess
        )

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit,
        onSuccess: (T) -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onError = onError,
            onComplete = onComplete,
            onSuccess = onSuccess
        )
}

fun <T : Any> Maybe<T>.wrap(): MaybeWrapper<T> = MaybeWrapper(this)
