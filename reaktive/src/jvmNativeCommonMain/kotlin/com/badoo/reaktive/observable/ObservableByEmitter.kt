package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

actual inline fun <T> observable(crossinline onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), ObservableEmitter<T> {
                override fun setDisposable(disposable: Disposable?) {
                    set(disposable)
                }

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

                private inline fun doIfNotDisposedAndDispose(block: () -> Unit) {
                    if (!isDisposed) {
                        val disposable: Disposable? = replace(null)
                        try {
                            dispose()
                            block()
                        } finally {
                            disposable?.dispose()
                        }
                    }
                }
            }

        observer.onSubscribe(emitter)
        emitter.tryCatch { onSubscribe(emitter) }
    }
