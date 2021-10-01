package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Filters elements emitted by the [Observable] using the provided [predicate].
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#filter-io.reactivex.functions.Predicate-).
 */
fun <T> Observable<T>.filter(predicate: (T) -> Boolean): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    emitter.tryCatch(block = { predicate(value) }) {
                        if (it) {
                            emitter.onNext(value)
                        }
                    }
                }
            }
        )
    }
