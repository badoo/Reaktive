package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), MaybeEmitter<T> {
                override fun onSuccess(value: T) {
                    if (!isDisposed) {
                        observer.onSuccess(value)
                        dispose()
                    }
                }

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
