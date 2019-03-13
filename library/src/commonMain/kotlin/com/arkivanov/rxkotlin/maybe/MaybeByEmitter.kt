package com.arkivanov.rxkotlin.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

inline fun <T> maybeByEmitter(crossinline onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T> =
    maybe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : MaybeEmitter<T> {
                override fun onSuccess(value: T) {
                    if (!disposableWrapper.isDisposed) {
                        try {
                            observer.onSuccess(value)
                        } finally {
                            disposableWrapper.dispose()
                        }
                    }
                }

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