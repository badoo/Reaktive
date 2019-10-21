package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), SingleEmitter<T> {
                override fun onSuccess(value: T) {
                    if (!isDisposed) {
                        observer.onSuccess(value)
                        dispose()
                    }
                }

                override fun onError(error: Throwable) {
                    if (!isDisposed) {
                        observer.onError(error)
                        dispose()
                    }
                }

                @Suppress("RedundantOverride") // IDEA complains that setDisposable is not implemented
                override fun setDisposable(disposable: Disposable) {
                    set(disposable)
                }
            }

        observer.onSubscribe(emitter)

        try {
            onSubscribe(emitter)
        } catch (e: Throwable) {
            emitter.onError(e)
        }
    }
