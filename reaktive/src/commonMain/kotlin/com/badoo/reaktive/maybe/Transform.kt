package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper

internal inline fun <T, R> Maybe<T>.transform(
    crossinline onSuccess: (value: T, onSuccess: (R) -> Unit, onComplete: () -> Unit) -> Unit
): Maybe<R> =
    maybe { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T> {
                private val onSuccessFunction = emitter::onSuccess
                private val onCompleteFunction = emitter::onComplete

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
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