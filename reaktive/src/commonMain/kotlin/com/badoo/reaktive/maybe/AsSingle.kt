package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleCallbacks
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.single

fun <T> Maybe<T>.asSingle(defaultValue: T): Single<T> =
    asSingleOrAction { observer ->
        observer.onSuccess(defaultValue)
    }

fun <T> Maybe<T>.asSingle(defaultValueSupplier: () -> T): Single<T> =
    asSingleOrAction { observer ->
        observer.tryCatch(block = defaultValueSupplier, onSuccess = observer::onSuccess)
    }

internal inline fun <T> Maybe<T>.asSingleOrAction(
    crossinline onComplete: (emitter: SingleEmitter<T>) -> Unit
): Single<T> =
    single { emitter ->
        subscribe(
            object : MaybeObserver<T>, SingleCallbacks<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    onComplete(emitter)
                }
            }
        )
    }
