package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Emit only the first [limit] elements emitted by source [Observable].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#take-long-).
 */
fun <T> Observable<T>.take(limit: Int): Observable<T> {
    require(limit >= 0) { "count >= 0 required but it was $limit" }

    return observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                private var remaining = limit

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)

                    if (remaining == 0) {
                        onComplete()
                    }
                }

                override fun onNext(value: T) {
                    if (remaining > 0) {
                        remaining--
                        val stop = remaining == 0
                        emitter.onNext(value)
                        if (stop) {
                            onComplete()
                        }
                    }
                }
            }
        )
    }
}
