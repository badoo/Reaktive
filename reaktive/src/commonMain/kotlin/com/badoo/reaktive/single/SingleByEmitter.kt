package com.badoo.reaktive.single

import com.badoo.reaktive.base.DisposableEmitter
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.doIfNotDisposedAndDispose

inline fun <T> single(crossinline onSubscribe: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val emitter =
            object : DisposableEmitter(), SingleEmitter<T> {
                override fun onSuccess(value: T) {
                    doIfNotDisposedAndDispose {
                        observer.onSuccess(value)
                    }
                }

                override fun onError(error: Throwable) {
                    doIfNotDisposedAndDispose {
                        observer.onError(error)
                    }
                }
            }

        observer.onSubscribe(emitter)
        emitter.tryCatch { onSubscribe(emitter) }
    }
