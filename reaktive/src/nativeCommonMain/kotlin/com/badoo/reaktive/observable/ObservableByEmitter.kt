package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

// Separate implementation prevents unnecessary freezing: https://github.com/badoo/Reaktive/issues/472
// Not inlined due to https://youtrack.jetbrains.com/issue/KT-44764
actual fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val serialDisposable = SerialDisposable()
        observer.onSubscribe(serialDisposable)

        val emitter =
            object : ObservableEmitter<T> {
                override val isDisposed: Boolean get() = serialDisposable.isDisposed

                override fun setDisposable(disposable: Disposable?) {
                    serialDisposable.set(disposable)
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
                        val disposable: Disposable? = serialDisposable.replace(null)
                        try {
                            serialDisposable.dispose()
                            block()
                        } finally {
                            disposable?.dispose()
                        }
                    }
                }
            }

        emitter.tryCatch { onSubscribe(emitter) }
    }
