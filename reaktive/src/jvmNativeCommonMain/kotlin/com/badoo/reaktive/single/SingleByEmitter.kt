package com.badoo.reaktive.single

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

actual inline fun <T> single(crossinline onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), SingleEmitter<T> {
                override fun setDisposable(disposable: Disposable?) {
                    set(disposable)
                }

                override fun onSuccess(value: T) {
                    doIfNotDisposedAndDispose {
                        observer.onSuccess(value)
                    }
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
