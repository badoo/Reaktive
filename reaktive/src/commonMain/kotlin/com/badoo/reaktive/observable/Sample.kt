package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.scheduler.Scheduler
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T> Observable<T>.sample(windowMillis: Long, scheduler: Scheduler): Observable<T> =
    observable { emitter ->
        val disposableWrapper = CompositeDisposable()
        emitter.setDisposable(disposableWrapper)
        val executor = scheduler.newExecutor()
        disposableWrapper += executor

        subscribeSafe(
            object : ObservableObserver<T> {
                private val lock = newLock()
                private lateinit var lastValue: MutableList<T>

                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper += disposable

                    executor.submitRepeating(periodMillis = windowMillis) {
                        lock.synchronized {
                            if (::lastValue.isInitialized) {
                                emitter.onNext(lastValue[0])
                            }
                        }
                    }
                }

                override fun onNext(value: T) {
                    lock.synchronized {
                        if (::lastValue.isInitialized) {
                            lastValue[0] = value
                        } else {
                            lastValue = mutableListOf(value)
                        }
                    }
                }

                override fun onComplete() {
                    executor.cancel()
                    executor.submit(task = emitter::onComplete)
                }

                override fun onError(error: Throwable) {
                    executor.cancel()
                    executor.submit { emitter.onError(error) }
                }
            }
        )
    }