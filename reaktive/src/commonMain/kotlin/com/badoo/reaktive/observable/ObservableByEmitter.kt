package com.badoo.reaktive.observable

import com.badoo.reaktive.base.DisposableEmitter
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.doIfNotDisposed

fun <T> observable(onSubscribe: (emitter: ObservableEmitter<T>) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val emitter =
            object : DisposableEmitter(), ObservableEmitter<T> {
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
            }

        observer.onSubscribe(emitter)
        emitter.tryCatch(block = { onSubscribe(emitter) })
    }
