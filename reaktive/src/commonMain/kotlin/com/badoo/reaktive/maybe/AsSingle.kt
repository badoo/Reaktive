package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleCallbacks
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

// TODO Make it inline/crossinline again when native compilation is fixed
internal fun <T> Maybe<T>.asSingleOrAction(onComplete: (observer: SingleObserver<T>) -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : MaybeObserver<T>, Observer by observer, SingleCallbacks<T> by observer {
                override fun onComplete() {
                    onComplete(observer)
                }
            }
        )
    }