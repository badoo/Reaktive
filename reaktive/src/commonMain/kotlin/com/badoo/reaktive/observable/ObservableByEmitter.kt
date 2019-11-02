package com.badoo.reaktive.observable

import com.badoo.reaktive.base.DisposableEmitter
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.doIfNotDisposedAndDispose

inline fun <T> observable(crossinline onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val emitter =
            object : DisposableEmitter(), ObservableEmitter<T> {
                override fun onNext(value: T) {
                    if (!isDisposed) {
                        observer.onNext(value)
                    }
                }

                override fun onComplete() {
                    doIfNotDisposedAndDispose(observer::onComplete)
                }

                override fun onError(error: Throwable) {
                    doIfNotDisposedAndDispose {
                        observer.onError(error)
                    }
                }
            }

        observer.onSubscribe(emitter)
        emitter.tryCatch { onSubscribe(emitter) }
    }
