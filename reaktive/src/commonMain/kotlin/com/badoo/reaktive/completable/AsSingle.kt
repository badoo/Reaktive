package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.singleUnsafe

fun <T> Completable.asSingle(defaultValue: T): Single<T> =
    asSingleOrAction { observer ->
        observer.onSuccess(defaultValue)
    }

fun <T> Completable.asSingle(defaultValueSupplier: () -> T): Single<T> =
    asSingleOrAction { observer ->
        observer.tryCatch(defaultValueSupplier, observer::onSuccess)
    }

private inline fun <T> Completable.asSingleOrAction(crossinline onComplete: (observer: SingleObserver<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    onComplete(observer)
                }
            }
        )
    }