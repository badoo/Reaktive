package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : MaybeEmitter<T> {
                override val isDisposed: Boolean get() = disposableWrapper.isDisposed

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
    