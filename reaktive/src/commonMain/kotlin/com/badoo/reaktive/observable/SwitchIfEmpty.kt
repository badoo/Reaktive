package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun <T> Observable<T>.switchIfEmpty(otherObservable: Observable<T>): Observable<T> =
    switchIfEmpty { otherObservable }

fun <T> Observable<T>.switchIfEmpty(otherObservable: () -> Observable<T>): Observable<T> =
    observable { emitter ->
        val serialDisposable = SerialDisposable()
        emitter.setDisposable(serialDisposable)

        subscribe(
            object : ObservableObserver<T>, ErrorCallback by emitter {
                private val isEmpty = AtomicBoolean(true)

                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
                }

                override fun onNext(value: T) {
                    isEmpty.value = false
                    emitter.onNext(value)
                }

                override fun onComplete() {
                    if (isEmpty.value) {
                        emitter.tryCatch(otherObservable) {
                            it.subscribeSafe(
                                object : ObservableObserver<T>, ObservableCallbacks<T> by emitter {
                                    override fun onSubscribe(disposable: Disposable) {
                                        serialDisposable.set(disposable)
                                    }
                                }
                            )
                        }
                    } else {
                        emitter.onComplete()
                    }
                }
            }
        )
    }
