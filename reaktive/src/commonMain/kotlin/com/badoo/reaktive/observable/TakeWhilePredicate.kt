package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Mirror items emitted by upstream until a specified condition becomes false.
 * See: [http://reactivex.io/documentation/operators/takewhile.html].
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
