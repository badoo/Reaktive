package com.badoo.reaktive.single

import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.utils.lock.newLock
import com.badoo.reaktive.utils.lock.synchronized

fun <T> Single<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                    action(disposable)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeSuccess(consumer: (T) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    consumer(value)
                    observer.onSuccess(value)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    consumer(error)
                    observer.onError(error)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeTerminate(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

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
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper.wrap(onBeforeDispose = action))

        subscribeSafe(
            object : SingleObserver<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeFinally(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val lock = newLock()
        var isFinished = false

        fun onFinally() {
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

        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper.wrap(onBeforeDispose = ::onFinally))

        subscribeSafe(
            object : SingleObserver<T> by observer {
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
            }
        )
    }
