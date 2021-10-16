package com.badoo.reaktive.observable

import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable

/**
 * Converts elements emitted by the [Observable] using the provided [mapper] and emits the result elements.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#map-io.reactivex.functions.Function-).
 */
fun <T, R> Observable<T>.map(mapper: (T) -> R): Observable<R> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    emitter.tryCatch(block = { mapper(value) }, onSuccess = emitter::onNext)
                }
            }
        )
    }
