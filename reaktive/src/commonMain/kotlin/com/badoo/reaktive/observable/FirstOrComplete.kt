package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.maybe.Maybe
import com.badoo.reaktive.maybe.maybe

/**
 * Returns a [Maybe] that emits only the very first element emitted by the source [Observable],
 * or completes if the source [Observable] is empty.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#firstElement--).
 */
fun <T> Observable<T>.firstOrComplete(): Maybe<T> =
    maybe { emitter ->
        val serialDisposable = SerialDisposable()
        emitter.setDisposable(serialDisposable)

        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
                }

                override fun onNext(value: T) {
                    emitter.onSuccess(value)
                    serialDisposable.dispose()
                }
            }
        )
    }
