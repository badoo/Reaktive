package com.badoo.reaktive.completable

import com.badoo.reaktive.annotations.UseReturnValue
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.utils.handleReaktiveError

/**
 * Subscribes to the [Completable] and provides event callbacks.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#subscribe-io.reactivex.functions.Action-io.reactivex.functions.Consumer-).
 */
@UseReturnValue
fun Completable.subscribe(
    onSubscribe: ((Disposable) -> Unit)? = null,
    onError: ((Throwable) -> Unit)? = null,
    onComplete: (() -> Unit)? = null
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

    subscribeSafe(
        object : CompletableObserver {
            override fun onSubscribe(disposable: Disposable) {
                serialDisposable.set(disposable)
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
