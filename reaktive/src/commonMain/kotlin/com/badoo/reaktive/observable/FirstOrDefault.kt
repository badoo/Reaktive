package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.singleUnsafe

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

internal inline fun <T> Observable<T>.firstOrAction(crossinline onComplete: (observer: SingleObserver<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : ObservableObserver<T>, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onNext(value: T) {
                    disposableWrapper.dispose()
                    observer.onSuccess(value)
                }

                override fun onComplete() {
                    onComplete(observer)
                }
            }
        )
    }