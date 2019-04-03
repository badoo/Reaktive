package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

inline fun completableByEmitter(crossinline onSubscribe: (emitter: CompletableEmitter) -> Unit): Completable =
    completable { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : CompletableEmitter {
                override fun onComplete() {
                    if (!disposableWrapper.isDisposed) {
                        try {
                            observer.onComplete()
                        } finally {
                            disposableWrapper.dispose()
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    if (!disposableWrapper.isDisposed) {
                        try {
                            observer.onError(error)
                        } finally {
                            disposableWrapper.dispose()
                        }
                    }
                }

                override fun setDisposable(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }

        try {
            onSubscribe(emitter)
        } catch (e: Throwable) {
            emitter.onError(e)
        }
    }