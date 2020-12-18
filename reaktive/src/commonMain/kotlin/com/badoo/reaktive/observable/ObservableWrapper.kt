package com.badoo.reaktive.observable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.subscribe as subscribeRx

/**
 * Wrappers are normally exposed to Swift.
 * You can also extend the wrapper class if you need to expose any additional operators.
 */
open class ObservableWrapper<out T : Any>(inner: Observable<T>) : Observable<T> by inner {

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onSubscribe: ((Disposable) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        onComplete: (() -> Unit)? = null,
        onNext: ((T) -> Unit)? = null
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onSubscribe = onSubscribe,
            onError = onError,
            onComplete = onComplete,
            onNext = onNext
        )

    @UseReturnValue
    fun subscribe(): Disposable = subscribeRx()

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onNext: (T) -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onNext = onNext
        )

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onComplete: () -> Unit,
        onNext: (T) -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onComplete = onComplete,
            onNext = onNext
        )

    @UseReturnValue
    fun subscribe(
        isThreadLocal: Boolean = false,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit,
        onNext: (T) -> Unit
    ): Disposable =
        subscribeRx(
            isThreadLocal = isThreadLocal,
            onError = onError,
            onComplete = onComplete,
            onNext = onNext
        )
}

fun <T : Any> Observable<T>.wrap(): ObservableWrapper<T> = ObservableWrapper(this)
