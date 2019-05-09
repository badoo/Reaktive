package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.single.Single
import com.badoo.reaktive.single.single
import com.badoo.reaktive.utils.atomicreference.AtomicReference

fun <T, C> Observable<T>.collect(initialCollection: C, accumulator: (C, T) -> C): Single<C> =
    single { emitter ->
        subscribeSafe(
            object : ObservableObserver<T>, ErrorCallback by emitter {
                private val collection = AtomicReference(initialCollection, true)

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    collection.value =
                        try {
                            accumulator(collection.value, value)
                        } catch (e: Throwable) {
                            emitter.onError(e)
                            return
                        }
                }

                override fun onComplete() {
                    emitter.onSuccess(collection.value)
                }
            }
        )
    }