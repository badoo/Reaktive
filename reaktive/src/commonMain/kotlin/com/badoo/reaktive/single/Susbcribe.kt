package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.utils.UseReturnValue
import com.badoo.reaktive.utils.handleSourceError

@UseReturnValue
fun <T> Single<T>.subscribe(
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onSuccess: ((T) -> Unit)? = null
): Disposable {
    val disposableWrapper = DisposableWrapper()
    onSubscribe?.invoke(disposableWrapper)

    subscribeSafe(
        object : SingleObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onSuccess(value: T) {
                try {
                    onSuccess?.invoke(value)
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
        }
    )

    return disposableWrapper
}