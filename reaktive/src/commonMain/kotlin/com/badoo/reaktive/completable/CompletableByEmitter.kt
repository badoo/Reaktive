package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun completable(onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable =
    completableUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), CompletableEmitter {
                override fun onComplete() {
                    if (!isDisposed) {
                        observer.onComplete()
                        dispose()
                    }
                }

                override fun onError(error: Throwable) {
                    if (!isDisposed) {
                        observer.onError(error)
                        dispose()
                    }
                }

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
