package com.badoo.reaktive.observable

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.Observer
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.utils.arrayqueue.ArrayQueue
import com.badoo.reaktive.utils.arrayqueue.isNotEmpty
import com.badoo.reaktive.utils.arrayqueue.take
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T, R> Observable<T>.concatMap(mapper: (T) -> Observable<R>): Observable<R> =
    observableByEmitter { emitter ->
        val disposables = CompositeDisposable()
        emitter.setDisposable(disposables)

        subscribeSafe(
            object : ObservableObserver<T> {
                private val lock = newLock()
                private val queue = ArrayQueue<T>()
                private var isDraining = false
                private var isUpstreamCompleted = false

                private val mappedObserver =
                    object : ObservableObserver<R>, Observer by this, ErrorCallback by this {
                        override fun onNext(value: R) {
                            lock.synchronized {
                                emitter.onNext(value)
                            }
                        }

                        override fun onComplete() {
                            drain()
                        }
                    }

                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onNext(value: T) {
                    lock
                        .synchronized {
                            queue.offer(value)
                            if (!isDraining) {
                                isDraining = true
                                true
                            } else {
                                false
                            }
                        }
                        .takeIf { it }
                        ?.also { drain() }
                }

                override fun onComplete() {
                    lock.synchronized {
                        isUpstreamCompleted = true
                        checkCompleted()
                    }
                }

                override fun onError(error: Throwable) {
                    lock.synchronized {
                        emitter.onError(error)
                    }
                }

                private fun drain() {
                    lock
                        .synchronized {
                            if (queue.isNotEmpty) {
                                queue.take()
                            } else {
                                isDraining = false
                                checkCompleted()
                                return
                            }
                        }
                        .let {
                            try {
                                mapper(it)
                            } catch (e: Throwable) {
                                onError(e)
                                return
                            }
                        }
                        .subscribeSafe(mappedObserver)
                }

                private fun checkCompleted() {
                    if (isUpstreamCompleted && !isDraining) {
                        emitter.onComplete()
                    }
                }
            }
        )
    }