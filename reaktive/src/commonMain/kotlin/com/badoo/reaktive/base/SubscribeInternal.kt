package com.badoo.reaktive.base

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.handleSourceError

internal fun <O, C> Source<O>.subscribeInternal(
    callbacks: C,
    observer: O
) where O : Observer, O : ErrorCallback, C : Disposable, C : SubscribeCallback, C : SubscribeErrorCallback {
    try {
        callbacks.onSubscribeCallback?.invoke(callbacks)
    } catch (e: Throwable) {
        try {
            handleSourceError(e, callbacks.onErrorCallback)
        } finally {
            callbacks.dispose()
        }

        return
    }

    subscribeSafe(observer)
}

internal fun <C> C.onError(error: Throwable) where C : Disposable, C : SubscribeErrorCallback {
    if (!checkDisposed(error)) {
        try {
            handleSourceError(error, onErrorCallback)
        } finally {
            dispose()
        }
    }
}

internal fun <C> C.onComplete() where C : Disposable, C : SubscribeCompleteCallback {
    if (!checkDisposed()) {
        try {
            onCompleteCallback?.invoke()
        } catch (e: Throwable) {
            handleSourceError(e)
        } finally {
            dispose()
        }
    }
}

internal fun <T, C> C.onSuccess(value: T) where C : Disposable, C : SubscribeSuccessCallback<T> {
    if (!checkDisposed()) {
        try {
            onSuccessCallback?.invoke(value)
        } catch (e: Throwable) {
            handleSourceError(e)
        } finally {
            dispose()
        }
    }
}

internal fun <T, C> C.onNext(value: T) where C : Disposable, C : SubscribeValueCallback<T>, C : SubscribeErrorCallback {
    if (!checkDisposed()) {
        try {
            onNextCallback?.invoke(value)
        } catch (e: Throwable) {
            onError(e)
        }
    }
}

private fun Disposable.checkDisposed(existingException: Throwable? = null): Boolean =
    try {
        isDisposed
    } catch (e: Throwable) {
        handleSourceError(if (existingException == null) e else CompositeException(existingException, e))
        true
    }

internal interface SubscribeCallback {
    val onSubscribeCallback: ((Disposable) -> Unit)?
}

internal interface SubscribeErrorCallback {
    val onErrorCallback: ((Throwable) -> Unit)?
}

internal interface SubscribeCompleteCallback {
    val onCompleteCallback: (() -> Unit)?
}

internal interface SubscribeSuccessCallback<T> {
    val onSuccessCallback: ((T) -> Unit)?
}

interface SubscribeValueCallback<T> {
    val onNextCallback: ((T) -> Unit)?
}