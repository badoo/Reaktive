package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single

/**
 * Collects elements emitted by the **finite** source [Observable] into a mutable data structure [C]
 * and returns a [Single] that emits this structure. The [accumulator] should mutate the structure
 * adding elements into it.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#collectInto-U-io.reactivex.functions.BiConsumer-).
 */
fun <T, C> Observable<T>.collect(collectionSupplier: () -> C, accumulator: (C, T) -> Unit): Single<C> =
    single { emitter ->
        subscribe(
            object : ObservableObserver<T>, ErrorCallback by emitter {
                private val collection = collectionSupplier()

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    emitter.tryCatch {
                        accumulator(collection, value)
                    }
                }

                override fun onComplete() {
                    emitter.onSuccess(collection)
                }
            }
        )
    }
