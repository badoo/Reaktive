package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.serializer.serializer

fun <T> Observable<T>.repeatUntil(predicate: () -> Boolean): Observable<T> =
    observable { emitter ->
        val observer =
            object : ObservableObserver<T>, ValueCallback<T> by emitter, ErrorCallback by emitter {
                // Prevents recursive subscriptions
                private val serializer =
                    serializer<Unit> {
                        subscribe(this)
                        true
                    }

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
                        })
                }

                fun subscribeToUpstream() {
                    serializer.accept(Unit)
                }
            }

        observer.subscribeToUpstream()
    }
