package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.atomicreference.AtomicReference
import com.badoo.reaktive.utils.atomicreference.update
import com.badoo.reaktive.utils.atomicreference.updateAndGet

fun <T, R> Observable<T>.flatMap(mapper: (T) -> Observable<R>): Observable<R> =
    observable { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val serializedEmitter = emitter.serialize()

        subscribeSafe(
            object : ObservableObserver<T>, ErrorCallback by serializedEmitter {
                private val activeSourceCount = AtomicReference(1)

                private val mappedObserver =
                    object : ObservableObserver<R>, Observer by this, CompletableCallbacks by this, ValueCallback<R> by serializedEmitter {
                    }

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    activeSourceCount.update { it + 1 }

                    val mappedSource =
                        try {
                            mapper(value)
                        } catch (e: Throwable) {
                            onError(e)
                            return
                        }


                    mappedSource.subscribeSafe(mappedObserver)
                }

                override fun onComplete() {
                    if (activeSourceCount.updateAndGet { it - 1 } <= 0) {
                        serializedEmitter.onComplete()
                    }
                }
            }
        )
    }