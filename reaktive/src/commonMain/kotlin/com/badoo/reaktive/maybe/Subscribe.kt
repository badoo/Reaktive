package com.badoo.reaktive.maybe

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.utils.handleReaktiveError

/**
 * Subscribes to the [Maybe] and provides event callbacks.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#subscribe-io.reactivex.functions.Consumer-io.reactivex.functions.Consumer-io.reactivex.functions.Action-).
 *
 * @param isThreadLocal see [Maybe.threadLocal]
 */
@UseReturnValue
fun <T> Maybe<T>.subscribe(
    isThreadLocal: Boolean = false,
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null,
    onSuccess: ((T) -> Unit)? = null
): Disposable {
    val serialDisposable = SerialDisposable()

    try {
        onSubscribe?.invoke(serialDisposable)
    } catch (e: Throwable) {
        try {
            handleReaktiveError(e, onError)
        } finally {
            serialDisposable.dispose()
        }

        return serialDisposable
    }

    val source = if (isThreadLocal) threadLocal() else this

    source.subscribeSafe(
        object : MaybeObserver<T> {
            override fun onSubscribe(disposable: Disposable) {
                serialDisposable.set(disposable)
            }

            override fun onSuccess(value: T) {
                serialDisposable.doIfNotDisposed(dispose = true) {
                    try {
                        onSuccess?.invoke(value)
                    } catch (e: Throwable) {
                        handleReaktiveError(e)
                    }
                }
            }

            override fun onComplete() {
                serialDisposable.doIfNotDisposed(dispose = true) {
                    try {
                        onComplete?.invoke()
                    } catch (e: Throwable) {
                        handleReaktiveError(e)
                    }
                }
            }

            override fun onError(error: Throwable) {
                serialDisposable.doIfNotDisposed(dispose = true) {
                    handleReaktiveError(error, onError)
                }
            }
        }
    )

    return serialDisposable
}
