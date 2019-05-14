package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

fun <T> Observable<T>.skip(count: Long): Observable<T> =
    observable { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                private var remaining = count

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    try {
                        if (remaining != 0L) {
                            remaining--
                        } else {
                            emitter.onNext(value)
                        }
                    } catch (e: Throwable) {
                        emitter.onError(e)
                    }
                }
            }
        )
    }