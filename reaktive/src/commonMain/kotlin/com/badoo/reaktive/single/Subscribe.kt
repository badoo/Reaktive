package com.badoo.reaktive.single

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.utils.handleReaktiveError

/**
 * Subscribes to the [Single] and provides event callbacks.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#subscribe-io.reactivex.functions.Consumer-io.reactivex.functions.Consumer-).
 *
 * @param isThreadLocal see [Single.threadLocal]
 */
@UseReturnValue
fun <T> Single<T>.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onSuccess: ((T) -> Unit)? = null
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
        object : SingleObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                disposableWrapper.set(disposable)
            }

            override fun onSuccess(value: T) {
                disposableWrapper.doIfNotDisposed(dispose = true) {
                    try {
                        onSuccess?.invoke(value)
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
