package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.utils.handleReaktiveError

@UseReturnValue
fun Completable.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()

    try {
        onSubscribe?.invoke(disposableWrapper)
    } catch (e: Throwable) {
        try {
            handleReaktiveError(e, onError)
        } finally {
            disposableWrapper.dispose()
        }

        return disposableWrapper
    }

    val source = if (isThreadLocal) threadLocal() else this

    source.subscribeSafe(
        object : CompletableObserver {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onComplete() {
                disposableWrapper.doIfNotDisposed(dispose = true) {
                    try {
                        onComplete?.invoke()
                    } catch (e: Throwable) {
                        handleReaktiveError(e)
                    }
                }
            }

            override fun onError(error: Throwable) {
                disposableWrapper.doIfNotDisposed(dispose = true) {
                    handleReaktiveError(error, onError)
                }
            }
        }
    )

    return disposableWrapper
}
