package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.observable.Observable
import com.badoo.reaktive.observable.observable
import com.badoo.reaktive.utils.serializer.serializer

fun <T> Single<T>.repeatUntil(predicate: (T) -> Boolean): Observable<T> =
    observable { emitter ->
        val observer =
            object : SingleObserver<T>, ErrorCallback by emitter {
                // Prevents recursive subscriptions
                private val serializer =
                    serializer<Unit> {
                        subscribe(this)
                        true
                    }

                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.onNext(value)

                    emitter.tryCatch(
                        block = { predicate(value) },
                        onSuccess = { shouldComplete ->
                            if (shouldComplete) {
                                emitter.onComplete()
                            } else if (!emitter.isDisposed) {
                                subscribeToUpstream()
                            }
                        })
                }

                fun subscribeToUpstream() {
                    serializer.accept(Unit)
                }
            }
        observer.subscribeToUpstream()
    }
