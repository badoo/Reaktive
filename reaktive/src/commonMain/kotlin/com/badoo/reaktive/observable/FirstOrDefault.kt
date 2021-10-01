package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.SingleEmitter
import com.badoo.reaktive.single.single

/**
 * Returns a [Single] that emits only the very first element emitted by the source [Observable],
 * or [defaultValue] if the source [Observable] is empty.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#first-T-).
 */
fun <T> Observable<T>.firstOrDefault(defaultValue: T): Single<T> =
    firstOrAction { emitter ->
        emitter.onSuccess(defaultValue)
    }

/**
 * Returns a [Single] that emits only the very first element emitted by the source [Observable],
 * or a value returned by [defaultValueSupplier] if the source [Observable] is empty.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#first-T-).
 */
fun <T> Observable<T>.firstOrDefault(defaultValueSupplier: () -> T): Single<T> =
    firstOrAction { emitter ->
        emitter.tryCatch(block = defaultValueSupplier, onSuccess = emitter::onSuccess)
    }

internal inline fun <T> Observable<T>.firstOrAction(
    crossinline onComplete: (emitter: SingleEmitter<T>) -> Unit
): Single<T> =
    single { emitter ->
        val serialDisposable = SerialDisposable()
        emitter.setDisposable(serialDisposable)

        subscribe(
            object : ObservableObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
                }

                override fun onNext(value: T) {
                    emitter.onSuccess(value)
                    serialDisposable.dispose()
                }

                override fun onComplete() {
                    onComplete(emitter)
                }
            }
        )
    }
