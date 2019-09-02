package com.badoo.reaktive.single

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.SubscribeCallback
import com.badoo.reaktive.base.SubscribeErrorCallback
import com.badoo.reaktive.base.SubscribeSuccessCallback
import com.badoo.reaktive.base.onError
import com.badoo.reaktive.base.onSuccess
import com.badoo.reaktive.base.subscribeInternal
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.ThreadLocalStorage

@UseReturnValue
fun <T> Single<T>.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onSuccess: ((T) -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()

    val callbacks =
        object : Callbacks<T>, Disposable by disposableWrapper {
            override val onSubscribeCallback: ((Disposable) -> Unit)? = onSubscribe
            override val onErrorCallback: ((Throwable) -> Unit)? = onError
            override val onSuccessCallback: ((T) -> Unit)? = onSuccess
        }

    if (isThreadLocal) {
        return subscribeThreadLocal(disposableWrapper, callbacks)
    }

    subscribeActual(disposableWrapper, callbacks)

    return callbacks
}

@UseReturnValue
private fun <T> Single<T>.subscribeThreadLocal(disposableWrapper: DisposableWrapper, callbacks: Callbacks<T>): Disposable {
    val storage = ThreadLocalStorage(callbacks)

    val threadLocalCallbacks =
        object : Callbacks<T> {
            override val onSubscribeCallback: ((Disposable) -> Unit)? get() = storage.value?.onSubscribeCallback
            override val onErrorCallback: ((Throwable) -> Unit)? get() = storage.value?.onErrorCallback
            override val onSuccessCallback: ((T) -> Unit)? get() = storage.value?.onSuccessCallback
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
private fun <T> Single<T>.subscribeActual(disposableWrapper: DisposableWrapper, callbacks: Callbacks<T>) {
    val observer =
        object : SingleObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onSuccess(value: T) {
                callbacks.onSuccess(value)
            }

            override fun onError(error: Throwable) {
                callbacks.onError(error)
            }
        }

    subscribeInternal(callbacks, observer)
}


private interface Callbacks<T> :
    SubscribeCallback,
    SubscribeErrorCallback,
    SubscribeSuccessCallback<T>,
    Disposable