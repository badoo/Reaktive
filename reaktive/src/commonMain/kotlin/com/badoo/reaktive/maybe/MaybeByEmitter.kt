package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.DisposableEmitter
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.doIfNotDisposedAndDispose

inline fun <T> maybe(crossinline onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val emitter =
            object : DisposableEmitter(), MaybeEmitter<T> {
                override fun onSuccess(value: T) {
                    doIfNotDisposedAndDispose {
                        observer.onSuccess(value)
                    }
                }

                override fun onComplete() {
                    doIfNotDisposedAndDispose(observer::onComplete)
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
