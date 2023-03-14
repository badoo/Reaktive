package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns an [Observable] that calls the [predicate] when this [Observable] completes
 * and resubscribes to this [Observable] if the [predicate] returned `false`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#repeatUntil-io.reactivex.functions.BooleanSupplier-).
 */
fun <T> Observable<T>.repeatUntil(predicate: () -> Boolean): Observable<T> =
    observable { emitter ->
        val observer =
            object : ObservableObserver<T>, ValueCallback<T> by emitter, ErrorCallback by emitter {
                private var recursiveGuard = 0

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    emitter.tryCatch(
                        block = predicate,
                        onSuccess = {
                            if (!emitter.isDisposed) {
                                if (it) {
                                    emitter.onComplete()
                                } else {
                                    subscribeToUpstream()
                                }
                            }
                        }
                    )
                }

                fun subscribeToUpstream() {
                    // Prevents recursive subscriptions
                    if (++recursiveGuard == 1) {
                        do {
                            subscribe(this)
                        } while (--recursiveGuard > 0)
                    }
                }
            }

        observer.subscribeToUpstream()
    }
