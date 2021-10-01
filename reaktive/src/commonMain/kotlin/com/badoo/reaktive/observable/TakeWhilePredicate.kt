package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Returns an [Observable] that checks the specified [predicate] for each element emitted
 * by the source [Observable], and emits the element if the [predicate] returned `true` or
 * completes if it returned `false`.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#takeWhile-io.reactivex.functions.Predicate-).
 */
fun <T> Observable<T>.takeWhile(predicate: (T) -> Boolean): Observable<T> =
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
                        } else {
                            emitter.onComplete()
                        }
                    }
                }
            }
        )
    }
