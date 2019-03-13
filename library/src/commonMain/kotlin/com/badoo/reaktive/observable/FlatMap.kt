package com.badoo.reaktive.observable

import com.badoo.reaktive.completable.CompletableObserver
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T, R> Observable<T>.flatMap(mapper: (T) -> Observable<R>): Observable<R> =
    observableByEmitter { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        subscribeSafe(
            object : ObservableObserver<T> {
                private val lock = newLock()
                private var activeSourceCount = 1

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    lock.synchronized {
                        activeSourceCount++
                    }

                    try {
                        mapper(value)
                    } catch (e: Throwable) {
                        onError(e)
                        return
                    }
                        .subscribeSafe(
                            object : ObservableObserver<R>, CompletableObserver by this {
                                override fun onNext(value: R) {
                                    lock.synchronized {
                                        emitter.onNext(value)
                                    }
                                }
                            }
                        )
                }

                override fun onComplete() {
                    lock.synchronized {
                        activeSourceCount--
                        if (activeSourceCount == 0) {
                            emitter.onComplete()
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    lock.synchronized {
                        emitter.onError(error)
                    }
                }
            }
        )
    }