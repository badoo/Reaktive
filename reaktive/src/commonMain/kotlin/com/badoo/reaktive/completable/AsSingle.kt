package com.badoo.reaktive.completable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.single

/**
 * Converts this [Completable] into a [Single] which emits the [defaultValue] when this [Completable] completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#toSingleDefault-T-).
 */
fun <T> Completable.asSingle(defaultValue: T): Single<T> =
    asSingleOrAction { observer ->
        observer.onSuccess(defaultValue)
    }

/**
 * Converts this [Completable] into a [Single] which emits a value returned by [defaultValueSupplier] when this [Completable] completes.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#toSingle-java.util.concurrent.Callable-).
 */
fun <T> Completable.asSingle(defaultValueSupplier: () -> T): Single<T> =
    asSingleOrAction { observer ->
        observer.tryCatch(block = defaultValueSupplier, onSuccess = observer::onSuccess)
    }

private inline fun <T> Completable.asSingleOrAction(
    crossinline onComplete: (observer: SingleEmitter<T>) -> Unit
): Single<T> =
    single { emitter ->
        subscribe(
            object : CompletableObserver, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    onComplete(emitter)
                }
            }
        )
    }
