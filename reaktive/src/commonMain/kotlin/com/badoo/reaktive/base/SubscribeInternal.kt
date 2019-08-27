package com.badoo.reaktive.base

import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.doIfNotDisposed
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
    doIfNotDisposed(dispose = true) {
        val callback: ((Throwable) -> Unit)? =
            try {
                onErrorCallback
            } catch (e: Throwable) {
                handleSourceError(CompositeException(error, e))
                return
            }

        handleSourceError(error, callback)
    }
}

internal fun <C> C.onComplete() where C : Disposable, C : SubscribeCompleteCallback {
    doIfNotDisposed(dispose = true) {
        val callback =
            try {
                onCompleteCallback ?: return
            } catch (e: Throwable) {
                handleSourceError(e)
                return
            }

        try {
            callback()
        } catch (e: Throwable) {
            handleSourceError(e)
        }
    }
}

internal fun <T, C> C.onSuccess(value: T) where C : Disposable, C : SubscribeSuccessCallback<T> {
    doIfNotDisposed(dispose = true) {
        val callback =
            try {
                onSuccessCallback ?: return
            } catch (e: Throwable) {
                handleSourceError(e)
                return
            }

        try {
            callback(value)
        } catch (e: Throwable) {
            handleSourceError(e)
        }
    }
}

internal fun <T, C> C.onNext(value: T) where C : Disposable, C : SubscribeValueCallback<T>, C : SubscribeErrorCallback {
    doIfNotDisposed {
        val callback =
            try {
                onNextCallback ?: return
            } catch (e: Throwable) {
                handleSourceError(e)
                return
            }

        try {
            callback(value)
        } catch (e: Throwable) {
            onError(e)
        }
    }
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