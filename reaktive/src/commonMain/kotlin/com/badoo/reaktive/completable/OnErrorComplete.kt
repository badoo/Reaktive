package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns a [Completable] which completes when this [Completable] signals `onError`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#onErrorComplete--).
 */
fun Completable.onErrorComplete(): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.onComplete()
                }
            }
        )
    }
