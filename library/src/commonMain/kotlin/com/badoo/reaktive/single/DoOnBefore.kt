package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T> Single<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    action(disposable)
                    observer.onSubscribe(disposable)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeSuccess(consumer: (T) -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSuccess(value: T) {
                    consumer(value)
                    observer.onSuccess(value)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onError(error: Throwable) {
                    consumer(error)
                    observer.onError(error)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeTerminate(action: () -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSuccess(value: T) {
                    action()
                    observer.onSuccess(value)
                }

                override fun onError(error: Throwable) {
                    action()
                    observer.onError(error)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeDispose(action: () -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = action))
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeFinally(action: () -> Unit): Single<T> =
    single { observer ->
        subscribeSafe(
            object : SingleObserver<T> by observer {
                private var lock = newLock()
                private var isFinished = false

                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = ::onFinally))
                }

                override fun onSuccess(value: T) {
                    onFinally()
                    observer.onSuccess(value)
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
