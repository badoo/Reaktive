package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

internal inline fun <T, R> Observable<T>.transform(crossinline onNext: (value: T, onNext: (R) -> Unit) -> Unit): Observable<R> =
    observable { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                private val onNextFunction = emitter::onNext

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    try {
                        onNext(value, onNextFunction)
                    } catch (e: Throwable) {
                        emitter.onError(e)
                    }
                }
            }
        )
    }