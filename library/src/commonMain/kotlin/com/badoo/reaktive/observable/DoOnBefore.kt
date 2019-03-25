package com.badoo.reaktive.observable

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T> Observable<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    action(disposable)
                    observer.onSubscribe(disposable)
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeNext(consumer: (T) -> Unit): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                override fun onNext(value: T) {
                    consumer(value)
                    observer.onNext(value)
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeComplete(action: () -> Unit): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                override fun onComplete() {
                    action()
                    observer.onComplete()
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                override fun onError(error: Throwable) {
                    consumer(error)
                    observer.onError(error)
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeTerminate(action: () -> Unit): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                override fun onComplete() {
                    action()
                    observer.onComplete()
                }

                override fun onError(error: Throwable) {
                    action()
                    observer.onError(error)
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeDispose(action: () -> Unit): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = action))
                }
            }
        )
    }

fun <T> Observable<T>.doOnBeforeFinally(action: () -> Unit): Observable<T> =
    observable { observer ->
        subscribeSafe(
            object : ObservableObserver<T> by observer {
                private val lock = newLock()
                private var isFinished = false

                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = ::onFinally))
                }

                override fun onComplete() {
                    onFinally()
                    observer.onComplete()
                }

                override fun onError(error: Throwable) {
                    onFinally()
                    observer.onError(error)
                }

                private fun onFinally() {
                    if (isFinished) {
                        return
                    }

                    lock.synchronized {
                        if (isFinished) {
                            return
                        }
                        isFinished = true
                    }

                    action()
                }
            }
        )
    }
