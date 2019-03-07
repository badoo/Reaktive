package com.badoo.reaktive.observable

import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T> Iterable<Observable<T>>.merge(): Observable<T> =
    observableByEmitter { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val lock = newLock()
        var activeSourceCount = 0

        forEach {
            lock.synchronized {
                activeSourceCount++
            }

            it.subscribeSafe(
                object : ObservableObserver<T> {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
                    }

                    override fun onNext(value: T) {
                        lock.synchronized {
                            emitter.onNext(value)
                        }
                    }

                    override fun onComplete() {
                        lock.synchronized {
                            if (--activeSourceCount == 0) {
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
    }

fun <T> merge(vararg sources: Observable<T>): Observable<T> =
    sources
        .asIterable()
        .merge()