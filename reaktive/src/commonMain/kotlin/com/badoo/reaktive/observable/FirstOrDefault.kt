package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.single

fun <T> Observable<T>.firstOrDefault(defaultValue: T): Single<T> =
    firstOrAction { emitter ->
        emitter.onSuccess(defaultValue)
    }

fun <T> Observable<T>.firstOrDefault(defaultValueSupplier: () -> T): Single<T> =
    firstOrAction { emitter ->
        emitter.tryCatch(block = defaultValueSupplier, onSuccess = emitter::onSuccess)
    }

internal inline fun <T> Observable<T>.firstOrAction(
    crossinline onComplete: (emitter: SingleEmitter<T>) -> Unit
): Single<T> =
    single { emitter ->
        val disposableWrapper = DisposableWrapper()
        emitter.setDisposable(disposableWrapper)

        subscribe(
            object : ObservableObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    emitter.onSuccess(value)
                    disposableWrapper.dispose()
                }

                override fun onComplete() {
                    onComplete(emitter)
                }
            }
        )
    }
