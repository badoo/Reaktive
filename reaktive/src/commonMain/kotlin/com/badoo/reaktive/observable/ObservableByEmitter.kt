package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed

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
                    doIfNotDisposed(dispose = true, block = observer::onComplete)
                }

                override fun onError(error: Throwable) {
                    doIfNotDisposed(dispose = true) {
                        observer.onError(error)
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
