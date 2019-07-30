package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleCallbacks
import com.badoo.reaktive.single.SingleObserver
import com.badoo.reaktive.single.singleUnsafe

fun <T> Maybe<T>.asSingle(defaultValue: T): Single<T> =
    asSingleOrAction { observer ->
        observer.onSuccess(defaultValue)
    }

fun <T> Maybe<T>.asSingle(defaultValueSupplier: () -> T): Single<T> =
    asSingleOrAction { observer ->
        observer.tryCatch(block = defaultValueSupplier, onSuccess = observer::onSuccess)
    }

internal inline fun <T> Maybe<T>.asSingleOrAction(crossinline onComplete: (observer: SingleObserver<T>) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, SingleCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    onComplete(observer)
                }
            }
        )
    }