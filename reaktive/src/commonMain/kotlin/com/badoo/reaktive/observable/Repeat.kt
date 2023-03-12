package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.serializer.serializer

/**
 * Returns an [Observable] that repeats the sequence of this [Observable] at most [times] times.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#repeat-long-).
 */
fun <T> Observable<T>.repeat(times: Long = Long.MAX_VALUE): Observable<T> {
    require(times >= 0L) { "Number of times must not be negative" }

    if (times == 0L) {
        return observableOfEmpty()
    }

    return observable { emitter ->
        val observer =
            object : ObservableObserver<T>, ValueCallback<T> by emitter, ErrorCallback by emitter {
                private var counter = times

                // Prevents recursive subscriptions
                private val serializer =
                    serializer<Unit> {
                        subscribe(this)
                        true
                    }

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    if ((counter == Long.MAX_VALUE) || (--counter > 0)) {
                        if (!emitter.isDisposed) {
                            subscribeToUpstream()
                        }
                    } else {
                        emitter.onComplete()
                    }
                }

                fun subscribeToUpstream() {
                    serializer.accept(Unit)
                }
            }

        observer.subscribeToUpstream()
    }
}
