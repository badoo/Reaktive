package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed

fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        val emitter =
            object : ObservableEmitter<T> {
                override val isDisposed: Boolean get() = disposableWrapper.isDisposed

                override fun onNext(value: T) {
                    if (!disposableWrapper.isDisposed) {
                        observer.onNext(value)
                    }
                }

                override fun onComplete() {
                    disposableWrapper.doIfNotDisposed(dispose = true, block = observer::onComplete)
                }

                override fun onError(error: Throwable) {
                    disposableWrapper.doIfNotDisposed(dispose = true) {
                        observer.onError(error)
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