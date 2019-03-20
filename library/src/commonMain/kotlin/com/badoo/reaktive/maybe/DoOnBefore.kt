package com.badoo.reaktive.maybe

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T> Maybe<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : MaybeObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    action(disposable)
                    observer.onSubscribe(disposable)
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeSuccess(consumer: (T) -> Unit): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : MaybeObserver<T> by observer {
                override fun onSuccess(value: T) {
                    consumer(value)
                    observer.onSuccess(value)
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeComplete(action: () -> Unit): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : MaybeObserver<T> by observer {
                override fun onComplete() {
                    action()
                    observer.onComplete()
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : MaybeObserver<T> by observer {
                override fun onError(error: Throwable) {
                    consumer(error)
                    observer.onError(error)
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeTerminate(action: () -> Unit): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : MaybeObserver<T> by observer {
                override fun onSuccess(value: T) {
                    action()
                    observer.onSuccess(value)
                }

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

fun <T> Maybe<T>.doOnBeforeDispose(action: () -> Unit): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : MaybeObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = action))
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeFinally(action: () -> Unit): Maybe<T> =
    maybe { observer ->
        subscribeSafe(
            object : MaybeObserver<T> by observer {
                private var lock = newLock()
                private var isFinished = false

                override fun onSubscribe(disposable: Disposable) {
                    observer.onSubscribe(disposable.wrap(onBeforeDispose = ::onFinally))
                }

                override fun onSuccess(value: T) {
                    onFinally()
                    observer.onSuccess(value)
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
