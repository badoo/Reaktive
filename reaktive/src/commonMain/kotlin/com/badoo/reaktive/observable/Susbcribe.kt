package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.UseReturnValue
import com.badoo.reaktive.utils.handleSourceError

@UseReturnValue
fun <T> Observable<T>.subscribe(
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onNext: ((T) -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()

    subscribeSafe(
        object : ObservableObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
                onSubscribe?.invoke(disposable)
            }

            override fun onNext(value: T) {
                onNext?.invoke(value)
            }

            override fun onComplete() {
                try {
                    onComplete?.invoke()
                } finally {
                    disposableWrapper.dispose()
                }
            }

            override fun onError(error: Throwable) {
                try {
                    handleSourceError(error, onError)
                } finally {
                    disposableWrapper.dispose()
                }
            }
        },
        onError
    )

    return disposableWrapper
}