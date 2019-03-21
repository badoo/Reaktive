package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.singleByEmitter

fun <T> Observable<T>.firstOrDefault(defaultValue: T): Single<T> =
    firstOrAction { emitter ->
        emitter.onSuccess(defaultValue)
    }

fun <T> Observable<T>.firstOrDefault(defaultValueSupplier: () -> T): Single<T> =
    firstOrAction { emitter ->
        try {
            defaultValueSupplier()
        } catch (e: Throwable) {
            emitter.onError(e)
            return@firstOrAction
        }
            .also(emitter::onSuccess)
    }

internal inline fun <T> Observable<T>.firstOrAction(crossinline onComplete: (emitter: SingleEmitter<T>) -> Unit): Single<T> =
    singleByEmitter { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    disposableWrapper.dispose()
                    emitter.onSuccess(value)
                }

                override fun onComplete() {
                    onComplete(emitter)
                }

                override fun onError(error: Throwable) {
                    emitter.onError(error)
                }
            }
        )
    }