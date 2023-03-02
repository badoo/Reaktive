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
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null
    ): Disposable =
        subscribeRx(
            onSubscribe = onSubscribe,
            onError = onError,
            onComplete = onComplete,
            onSuccess = onSuccess
        )

    @UseReturnValue
    fun subscribe(): Disposable = subscribeRx()

    @UseReturnValue
    fun subscribe(
        onSuccess: (T) -> Unit
    ): Disposable =
        subscribeRx(
            onSuccess = onSuccess
        )

    @UseReturnValue
    fun subscribe(
        onComplete: () -> Unit,
        onSuccess: (T) -> Unit
    ): Disposable =
        subscribeRx(
            onComplete = onComplete,
            onSuccess = onSuccess
        )

    @UseReturnValue
    fun subscribe(
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit,
        onSuccess: (T) -> Unit
    ): Disposable =
        subscribeRx(
            onError = onError,
            onComplete = onComplete,
            onSuccess = onSuccess
        )
}

fun <T : Any> Maybe<T>.wrap(): MaybeWrapper<T> = MaybeWrapper(this)
