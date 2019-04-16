package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : ObservableEmitter<T> {
                override fun onNext(value: T) {
                    if (!disposableWrapper.isDisposed) {
                        observer.onNext(value)
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