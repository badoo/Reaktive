package com.badoo.reaktive.maybe

import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.single

fun <T> Maybe<T>.asSingle(defaultValue: T): Single<T> =
    asSingleOrAction { observer ->
        observer.onSuccess(defaultValue)
    }

fun <T> Maybe<T>.asSingle(defaultValueSupplier: () -> T): Single<T> =
    asSingleOrAction { observer ->
        try {
            defaultValueSupplier()
        } catch (e: Throwable) {
            observer.onError(e)
            return@asSingleOrAction
        }
            .also(observer::onSuccess)
    }

internal inline fun <T> Maybe<T>.asSingleOrAction(crossinline onComplete: (observer: SingleObserver<T>) -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : MaybeObserver<T>, SingleObserver<T> by observer {
                override fun onComplete() {
                    onComplete(observer)
                }
            }
        )
    }