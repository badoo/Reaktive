package com.badoo.reaktive.completable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun Iterable<Completable>.merge(): Completable =
    completableByEmitter { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)
        val lock = newLock()
        var activeSourceCount = 0

        forEach {
            lock.synchronized {
                activeSourceCount++
            }

            it.subscribeSafe(
                object : CompletableObserver {
                    override fun onSubscribe(disposable: Disposable) {
                        disposables += disposable
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


fun merge(vararg sources: Completable): Completable =
    sources
        .asIterable()
        .merge()