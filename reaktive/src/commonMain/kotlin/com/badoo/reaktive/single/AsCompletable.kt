package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns a [Completable] that ignores the success value of this [Single]
 * and signals `onComplete` instead.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#ignoreElement--).
 */
fun Single<*>.asCompletable(): Completable =
    completable { emitter ->
        subscribe(
            object : SingleObserver<Any?>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: Any?) {
                    emitter.onComplete()
                }
            }
        )
    }
