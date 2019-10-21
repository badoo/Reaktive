package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed

fun <T> maybe(onSubscribe: (emitter: MaybeEmitter<T>) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val emitter =
            object : DisposableWrapper(), MaybeEmitter<T> {
                override fun onSuccess(value: T) {
                    doIfNotDisposed(dispose = true) {
                        observer.onSuccess(value)
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
