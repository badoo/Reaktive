package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Converts the value emitted by the [Maybe] using the provided [mapper] and emits the result.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#map-io.reactivex.functions.Function-).
 */
fun <T, R> Maybe<T>.map(mapper: (T) -> R): Maybe<R> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch(block = { mapper(value) }, onSuccess = emitter::onSuccess)
                }
            }
        )
    }
