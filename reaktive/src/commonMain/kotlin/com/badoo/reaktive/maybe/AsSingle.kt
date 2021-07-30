package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleCallbacks
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.single

/**
 * Converts this [Maybe] into a [Single], which signals either a success value (if this [Maybe] succeeds)
 * or the [defaultValue] (if this [Maybe] completes).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#toSingle-T-).
 */
fun <T> Maybe<T>.asSingle(defaultValue: T): Single<T> =
    asSingleOrAction { observer ->
        observer.onSuccess(defaultValue)
    }

/**
 * Converts this [Maybe] into a [Single], which signals either a success value (if this [Maybe] succeeds)
 * or a value returned by [defaultValueSupplier] (if this [Maybe] completes).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#toSingle-T-).
 */
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
