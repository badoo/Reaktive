package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), ObservableEmitter<T> {
                override fun onNext(value: T) {
                    if (!isDisposed) {
                        observer.onNext(value)
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
