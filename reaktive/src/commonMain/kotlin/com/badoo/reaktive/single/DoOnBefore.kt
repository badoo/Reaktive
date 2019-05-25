package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.utils.atomicreference.AtomicReference

fun <T> Single<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T>, SingleCallbacks<T> by observer {
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
            object : SingleObserver<T>, ErrorCallback by observer {
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
            object : SingleObserver<T>, SuccessCallback<T> by observer {
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
            object : SingleObserver<T> {
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
            object : SingleObserver<T>, SingleCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeFinally(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val isFinished = AtomicReference(false)

        fun onFinally() {
            @Suppress("BooleanLiteralArgument") // Not allowed for expected classes
            if (isFinished.compareAndSet(false, true)) {
                action()
            }
        }

        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper.wrap(onBeforeDispose = ::onFinally))

        subscribeSafe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
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
