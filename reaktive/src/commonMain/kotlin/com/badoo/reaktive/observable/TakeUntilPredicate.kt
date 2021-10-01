package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable

/**
 * Returns an [Observable] that emits elements emitted by the source [Observable],
 * checks the specified [predicate] for each element and completes when it returned `true`
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#takeUntil-io.reactivex.functions.Predicate-).
 */
fun <T> Observable<T>.takeUntil(predicate: (T) -> Boolean): Observable<T> =
    observable { emitter ->
        val serialDisposable = SerialDisposable()
        emitter.setDisposable(serialDisposable)

        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
                }

                override fun onNext(value: T) {
                    emitter.onNext(value)

                    emitter.tryCatch(block = { predicate(value) }) {
                        if (it) {
                            emitter.onComplete()
                        }
                    }
                }
            }
        )
    }
