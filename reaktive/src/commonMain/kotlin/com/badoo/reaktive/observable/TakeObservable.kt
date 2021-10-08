package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.utils.atomic.AtomicInt

/**
 * Emit only the first [limit] elements emitted by source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#take-long-).
 */
fun <T> Observable<T>.take(limit: Int): Observable<T> {
    require(limit >= 0) { "count >= 0 required but it was $limit" }

    return observable { emitter ->
        val serialDisposable = SerialDisposable()
        emitter.setDisposable(serialDisposable)

        val remaining = AtomicInt(limit)

        subscribe(object : ObservableObserver<T>, CompletableCallbacks by emitter {
            override fun onSubscribe(disposable: Disposable) {
                serialDisposable.set(disposable)

                if (remaining.value == 0) {
                    onComplete()
                }
            }

            override fun onNext(value: T) {
                if (remaining.value > 0) {
                    val stop = remaining.addAndGet(-1) == 0
                    emitter.onNext(value)
                    if (stop) {
                        onComplete()
                    }
                }
            }
        })
    }
}
