package com.badoo.reaktive.maybe

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns a [Completable] which signals `onComplete` when this [Maybe] signals either `onSuccess` or `onComplete`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Maybe.html#ignoreElement--).
 */
fun Maybe<*>.asCompletable(): Completable =
    completable { emitter ->
        subscribe(
            object : MaybeObserver<Any?>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: Any?) {
                    emitter.onComplete()
                }
            }
        )
    }
