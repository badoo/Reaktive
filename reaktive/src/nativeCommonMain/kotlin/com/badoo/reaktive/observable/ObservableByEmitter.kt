package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

// Separate implementation prevents unnecessary freezing: https://github.com/badoo/Reaktive/issues/472
actual inline fun <T> observable(crossinline onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : ObservableEmitter<T> {
                override val isDisposed: Boolean get() = disposableWrapper.isDisposed

                override fun setDisposable(disposable: Disposable?) {
                    disposableWrapper.set(disposable)
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
                        val disposable: Disposable? = disposableWrapper.replace(null)
                        try {
                            disposableWrapper.dispose()
                            block()
                        } finally {
                            disposable?.dispose()
                        }
                    }
                }
            }

        emitter.tryCatch { onSubscribe(emitter) }
    }
