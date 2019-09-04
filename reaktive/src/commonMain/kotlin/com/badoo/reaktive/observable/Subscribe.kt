package com.badoo.reaktive.observable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.SubscribeCallback
import com.badoo.reaktive.base.SubscribeCompleteCallback
import com.badoo.reaktive.base.SubscribeErrorCallback
import com.badoo.reaktive.base.SubscribeValueCallback
import com.badoo.reaktive.base.onComplete
import com.badoo.reaktive.base.onError
import com.badoo.reaktive.base.onNext
import com.badoo.reaktive.base.subscribeInternal
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.ThreadLocalStorage

@UseReturnValue
fun <T> Observable<T>.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onNext: ((T) -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()

    val callbacks =
        object : Callbacks<T> {
            override val onSubscribeCallback: ((Disposable) -> Unit)? = onSubscribe
            override val onErrorCallback: ((Throwable) -> Unit)? = onError
            override val onCompleteCallback: (() -> Unit)? = onComplete
            override val onNextCallback: ((T) -> Unit)? = onNext

            override val isDisposed: Boolean
                get() = disposableWrapper.isDisposed

            override fun dispose() {
                disposableWrapper.dispose()
            }
        }

    if (isThreadLocal) {
        return subscribeThreadLocal(disposableWrapper, callbacks)
    }

    subscribeActual(disposableWrapper, callbacks)

    return callbacks
}

@UseReturnValue
private fun <T> Observable<T>.subscribeThreadLocal(disposableWrapper: DisposableWrapper, callbacks: Callbacks<T>): Disposable {
    val storage = ThreadLocalStorage(callbacks)

    val threadLocalCallbacks =
        object : Callbacks<T> {
            override val onSubscribeCallback: ((Disposable) -> Unit)? get() = storage.value?.onSubscribeCallback
            override val onErrorCallback: ((Throwable) -> Unit)? get() = storage.value?.onErrorCallback
            override val onCompleteCallback: (() -> Unit)? get() = storage.value?.onCompleteCallback
            override val onNextCallback: ((T) -> Unit)? get() = storage.value?.onNextCallback
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
private fun <T> Observable<T>.subscribeActual(disposableWrapper: DisposableWrapper, callbacks: Callbacks<T>) {
    val observer =
        object : ObservableObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onNext(value: T) {
                callbacks.onNext(value)
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

private interface Callbacks<T> :
    SubscribeCallback,
    SubscribeErrorCallback,
    SubscribeValueCallback<T>,
    SubscribeCompleteCallback,
    Disposable