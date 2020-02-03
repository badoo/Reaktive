package com.badoo.reaktive.observable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.subscribe as subscribeRx

/**
 * Wrappers are normally exposed to Swift.
 * You might want to enable Objective-C generics,
 * please refer to the [documentation][https://kotlinlang.org/docs/reference/native/objc_interop.html#to-use]
 * for more information.
 * You can also extend the wrapper class if you need to expose any additional operators.
 */
open class ObservableWrapper<out T>(inner: Observable<T>) : Observable<T> by inner {

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
}

fun <T> Observable<T>.wrap(): ObservableWrapper<T> = ObservableWrapper(this)
