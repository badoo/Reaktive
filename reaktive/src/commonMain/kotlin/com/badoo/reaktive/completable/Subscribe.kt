package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.SubscribeCallback
import com.badoo.reaktive.base.SubscribeCompleteCallback
import com.badoo.reaktive.base.SubscribeErrorCallback
import com.badoo.reaktive.base.onComplete
import com.badoo.reaktive.base.onError
import com.badoo.reaktive.base.subscribeInternal
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.ThreadLocalStorage

@UseReturnValue
fun Completable.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()

    val callbacks =
        object : Callbacks, Disposable by disposableWrapper {
            override val onSubscribeCallback: ((Disposable) -> Unit)? = onSubscribe
            override val onErrorCallback: ((Throwable) -> Unit)? = onError
            override val onCompleteCallback: (() -> Unit)? = onComplete
        }

    if (isThreadLocal) {
        return subscribeThreadLocal(disposableWrapper, callbacks)
    }

    subscribeActual(disposableWrapper, callbacks)

    return callbacks
}

@UseReturnValue
private fun Completable.subscribeThreadLocal(disposableWrapper: DisposableWrapper, callbacks: Callbacks): Disposable {
    val storage = ThreadLocalStorage(callbacks)

    val threadLocalCallbacks =
        object : Callbacks {
            override val onSubscribeCallback: ((Disposable) -> Unit)? get() = storage.value?.onSubscribeCallback
            override val onErrorCallback: ((Throwable) -> Unit)? get() = storage.value?.onErrorCallback
            override val onCompleteCallback: (() -> Unit)? get() = storage.value?.onCompleteCallback
            override val isDisposed: Boolean get() = storage.value?.isDisposed ?: true

            override fun dispose() {
                storage.value?.dispose()
                storage.dispose()
            }
        }

    subscribeActual(disposableWrapper, threadLocalCallbacks)

    return threadLocalCallbacks
}

@UseReturnValue
private fun Completable.subscribeActual(disposableWrapper: DisposableWrapper, callbacks: Callbacks) {
    val observer =
        object : CompletableObserver {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onComplete() {
                callbacks.onComplete()
            }

            override fun onError(error: Throwable) {
                callbacks.onError(error)
            }
        }

    subscribeInternal(callbacks, observer)
}

private interface Callbacks :
    SubscribeCallback,
    SubscribeErrorCallback,
    SubscribeCompleteCallback,
    Disposable