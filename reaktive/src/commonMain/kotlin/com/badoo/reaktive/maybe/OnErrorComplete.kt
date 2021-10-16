package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns a [Maybe] which completes when this [Maybe] signals `onError`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#onErrorComplete--).
 */
fun <T> Maybe<T>.onErrorComplete(): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.onComplete()
                }
            }
        )
    }
