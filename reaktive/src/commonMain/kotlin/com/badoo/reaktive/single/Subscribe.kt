package com.badoo.reaktive.single

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.handleSourceError
import com.badoo.reaktive.utils.threadlocal.ThreadLocalStorage

@UseReturnValue
fun <T> Single<T>.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onSuccess: ((T) -> Unit)? = null
): Disposable {
    val callbacks =
        object : Callbacks<T> {
            override val onSubscribe: ((Disposable) -> Unit)? = onSubscribe
            override val onError: ((Throwable) -> Unit)? = onError
            override val onSuccess: ((T) -> Unit)? = onSuccess

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
private fun <T> Single<T>.subscribeThreadLocal(callbacks: Callbacks<T>): Disposable {
    val storage = ThreadLocalStorage(callbacks)

    return subscribeActual(
        object : Callbacks<T> {
            override val onSubscribe: ((Disposable) -> Unit)? get() = storage.value?.onSubscribe
            override val onError: ((Throwable) -> Unit)? get() = storage.value?.onError
            override val onSuccess: ((T) -> Unit)? get() = storage.value?.onSuccess

            override fun dispose() {
                storage.value?.dispose()
                storage.dispose()
            }
        }
    )
}

@UseReturnValue
private fun <T> Single<T>.subscribeActual(callbacks: Callbacks<T>): Disposable {
    val disposableWrapper = DisposableWrapper()
    callbacks.onSubscribe?.invoke(disposableWrapper)

    subscribeSafe(
        object : SingleObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onSuccess(value: T) {
                try {
                    callbacks.onSuccess?.invoke(value)
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
    val onSuccess: ((T) -> Unit)?

    fun dispose()
}