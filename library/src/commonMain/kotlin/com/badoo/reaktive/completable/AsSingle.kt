package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.single

fun <T> Completable.asSingle(defaultValue: T): Single<T> =
    asSingleOrAction { observer ->
        observer.onSuccess(defaultValue)
    }

fun <T> Completable.asSingle(defaultValueSupplier: () -> T): Single<T> =
    asSingleOrAction { observer ->
        try {
            defaultValueSupplier()
        } catch (e: Throwable) {
            observer.onError(e)
            return@asSingleOrAction
        }
            .also(observer::onSuccess)
    }

private inline fun <T> Completable.asSingleOrAction(crossinline onComplete: (observer: SingleObserver<T>) -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : CompletableObserver, Observer by observer, ErrorCallback by observer {
                override fun onComplete() {
                    onComplete(observer)
                }
            }
        )
    }