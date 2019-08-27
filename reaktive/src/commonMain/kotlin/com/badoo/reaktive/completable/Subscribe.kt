package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.handleSourceError
import com.badoo.reaktive.utils.ThreadLocalStorage

@UseReturnValue
fun Completable.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): Disposable {
    val callbacks =
        object : Callbacks {
            override val onSubscribe: ((Disposable) -> Unit)? = onSubscribe
            override val onError: ((Throwable) -> Unit)? = onError
            override val onComplete: (() -> Unit)? = onComplete

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
private fun Completable.subscribeThreadLocal(callbacks: Callbacks): Disposable {
    val storage = ThreadLocalStorage(callbacks)

    return subscribeActual(
        object : Callbacks {
            override val onSubscribe: ((Disposable) -> Unit)? get() = storage.value?.onSubscribe
            override val onError: ((Throwable) -> Unit)? get() = storage.value?.onError
            override val onComplete: (() -> Unit)? get() = storage.value?.onComplete

            override fun dispose() {
                storage.value?.dispose()
                storage.dispose()
            }
        }
    )
}

@UseReturnValue
private fun Completable.subscribeActual(callbacks: Callbacks): Disposable {
    val disposableWrapper = DisposableWrapper()
    callbacks.onSubscribe?.invoke(disposableWrapper)

    subscribeSafe(
        object : CompletableObserver {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
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

private interface Callbacks {
    val onSubscribe: ((Disposable) -> Unit)?
    val onError: ((Throwable) -> Unit)?
    val onComplete: (() -> Unit)?

    fun dispose()
}