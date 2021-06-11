package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable

/**
 * Converts the value emitted by the [Single] using the provided [mapper] and emits the result.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#map-io.reactivex.functions.Function-).
 */
fun <T, R> Single<T>.map(mapper: (T) -> R): Single<R> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch(block = { mapper(value) }, onSuccess = emitter::onSuccess)
                }
            }
        )
    }
