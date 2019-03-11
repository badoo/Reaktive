package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T> Observable<T>.debounce(timeout: Long, scheduler: Scheduler): Observable<T> =
    observableByEmitter { emitter ->
        val disposableWrapper = CompositeDisposable()
        emitter.setDisposable(disposableWrapper)
        val executor = scheduler.newExecutor()
        disposableWrapper += executor

        subscribeSafe(
            object : ObservableObserver<T> {
                private val lock = newLock()
                private var pendingValue: T? = null
                private var pendingValueCounter = 0

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper += disposable
                }

                override fun onNext(value: T) {
                    val valueCounter =
                        lock.synchronized {
                            pendingValue = value
                            ++pendingValueCounter
                        }

                    executor.cancel()

                    executor.submit(timeout) {
                        lock.synchronized {
                            if (pendingValueCounter == valueCounter) {
                                pendingValue = null
                                pendingValueCounter = 0
                            }
                        }

                        emitter.onNext(value)
                    }
                }

                override fun onComplete() {
                    executor.cancel()

                    executor.submit {
                        lock
                            .synchronized(::pendingValueCounter)
                            .takeIf { it > 0 }
                            ?.also {
                                @Suppress("UNCHECKED_CAST")
                                emitter.onNext(pendingValue as T)
                            }

                        emitter.onComplete()
                    }
                }

                override fun onError(error: Throwable) {
                    executor.cancel()

                    executor.submit {
                        emitter.onError(error)
                    }
                }
            }
        )
    }