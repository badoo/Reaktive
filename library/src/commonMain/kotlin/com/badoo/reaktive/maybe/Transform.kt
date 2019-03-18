package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable

internal inline fun <T, R> Maybe<T>.transform(
    crossinline onSuccess: (value: T, onSuccess: (R) -> Unit, onComplete: () -> Unit) -> Unit
): Maybe<R> =
    maybeByEmitter { emitter ->
        subscribeSafe(
            object : MaybeObserver<T> {
                private val onSuccessFunction = emitter::onSuccess
                private val onCompleteFunction = emitter::onComplete

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    try {
                        onSuccess(value, onSuccessFunction, onCompleteFunction)
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
            }
        )
    }