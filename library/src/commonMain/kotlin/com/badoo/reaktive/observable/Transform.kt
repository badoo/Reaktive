package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable

internal inline fun <T, R> Observable<T>.transform(crossinline onNext: (value: T, onNext: (R) -> Unit) -> Unit): Observable<R> =
    observableByEmitter { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, (R) -> Unit {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    try {
                        onNext(value, this)
                    } catch (e: Throwable) {
                        emitter.onError(e)
                    }
                }

                override fun onComplete() {
                    emitter.onComplete()
                }

                override fun onError(error: Throwable) {
                    emitter.onError(error)
                }

                override fun invoke(value: R) {
                    emitter.onNext(value)
                }
            }
        )
    }