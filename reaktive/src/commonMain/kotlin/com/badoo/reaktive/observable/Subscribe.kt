package com.badoo.reaktive.observable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.handleSourceError
import com.badoo.reaktive.utils.ThreadLocalStorage

@UseReturnValue
fun <T> Observable<T>.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onNext: ((T) -> Unit)? = null
): Disposable {
    val callbacks =
        object : Callbacks<T> {
            override val onSubscribe: ((Disposable) -> Unit)? = onSubscribe
            override val onError: ((Throwable) -> Unit)? = onError
            override val onComplete: (() -> Unit)? = onComplete
            override val onNext: ((T) -> Unit)? = onNext

            override fun dispose() {
                // no-op
            }
        }

    return if (isThreadLocal) {
        subscribeThreadLocal(callbacks)
    } else {
        subscribeActual(callbacks)
    }
}

@UseReturnValue
private fun <T> Observable<T>.subscribeThreadLocal(callbacks: Callbacks<T>): Disposable {
    val storage = ThreadLocalStorage(callbacks)

    return subscribeActual(
        object : Callbacks<T> {
            override val onSubscribe: ((Disposable) -> Unit)? get() = storage.value?.onSubscribe
            override val onError: ((Throwable) -> Unit)? get() = storage.value?.onError
            override val onComplete: (() -> Unit)? get() = storage.value?.onComplete
            override val onNext: ((T) -> Unit)? get() = storage.value?.onNext

            override fun dispose() {
                storage.value?.dispose()
                storage.dispose()
            }
        }
    )
}

@UseReturnValue
private fun <T> Observable<T>.subscribeActual(callbacks: Callbacks<T>): Disposable {
    val disposableWrapper = DisposableWrapper()
    callbacks.onSubscribe?.invoke(disposableWrapper)

    subscribeSafe(
        object : ObservableObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onNext(value: T) {
                callbacks.onNext?.invoke(value)
            }

            override fun onComplete() {
                try {
                    callbacks.onComplete?.invoke()
                } finally {
                    disposableWrapper.dispose()
                }
            }

            override fun onError(error: Throwable) {
                try {
                    handleSourceError(error, callbacks.onError)
                } finally {
                    disposableWrapper.dispose()
                }
            }
        }
    )

    return object : Disposable by disposableWrapper {
        override fun dispose() {
            disposableWrapper.dispose()
            callbacks.dispose()
        }
    }
}

private interface Callbacks<T> {
    val onSubscribe: ((Disposable) -> Unit)?
    val onError: ((Throwable) -> Unit)?
    val onComplete: (() -> Unit)?
    val onNext: ((T) -> Unit)?

    fun dispose()
}