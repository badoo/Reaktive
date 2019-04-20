package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> single(onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : SingleEmitter<T> {
                override fun onSuccess(value: T) {
                    if (!disposableWrapper.isDisposed) {
                        try {
                            observer.onSuccess(value)
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