package com.badoo.reaktive.observable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.utils.handleSourceError

@UseReturnValue
fun <T> Observable<T>.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onNext: ((T) -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()

    try {
        onSubscribe?.invoke(disposableWrapper)
    } catch (e: Throwable) {
        try {
            handleSourceError(e, onError)
        } finally {
            disposableWrapper.dispose()
        }

        return disposableWrapper
    }

    val source = if (isThreadLocal) threadLocal() else this

    source.subscribeSafe(
        object : ObservableObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onNext(value: T) {
                disposableWrapper.doIfNotDisposed {
                    try {
                        onNext?.invoke(value)
                    } catch (e: Throwable) {
                        onError(e)
                    }
                }
            }

            override fun onComplete() {
                disposableWrapper.doIfNotDisposed(dispose = true) {
                    try {
                        onComplete?.invoke()
                    } catch (e: Throwable) {
                        handleSourceError(e)
                    }
                }
            }

            override fun onError(error: Throwable) {
                disposableWrapper.doIfNotDisposed(dispose = true) {
                    handleSourceError(error, onError)
                }
            }
        }
    )

    return disposableWrapper
}