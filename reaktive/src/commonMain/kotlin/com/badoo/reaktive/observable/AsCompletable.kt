package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.Completable
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.completable.completable
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns a [Completable] which signals `onComplete` when this [Observable] signals `onComplete`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#ignoreElements--).
 */
fun Observable<*>.asCompletable(): Completable =
    completable { emitter ->
        subscribe(
            object : ObservableObserver<Any?>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: Any?) {
                    // no-op
                }
            }
        )
    }
