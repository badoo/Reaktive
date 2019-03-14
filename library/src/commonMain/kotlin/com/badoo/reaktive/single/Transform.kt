package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable

internal inline fun <T, R> Single<T>.transform(
    crossinline onSuccess: (value: T, onSuccess: (R) -> Unit) -> Unit
): Single<R> =
    singleByEmitter { emitter ->
        subscribeSafe(
            object : SingleObserver<T>, (R) -> Unit {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    try {
                        onSuccess(value, this)
                    } catch (e: Throwable) {
                        emitter.onError(e)
                    }
                }

                override fun onError(error: Throwable) {
                    emitter.onError(error)
                }

                override fun invoke(value: R) {
                    emitter.onSuccess(value)
                }
            }
        )
    }