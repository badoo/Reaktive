package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun <T> Single<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onSubscribe(disposableWrapper)
            observer.onError(e)

            return@singleUnsafe
        }

        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T>, SingleCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
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
                    observer.tryCatch({ consumer(value) }) {
                        observer.onSuccess(value)
                    }
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
                    observer.tryCatch({ consumer(error) }, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
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
                    observer.tryCatch(action) {
                        observer.onSuccess(value)
                    }
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch(action, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

fun <T> Single<T>.doOnBeforeDispose(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(
            object : Disposable by disposableWrapper {
                override fun dispose() {
                    observer.tryCatch(action)
                    disposableWrapper.dispose()
                }
            }
        )

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
        val isFinished = AtomicBoolean()

        val onFinally =
            {
                @Suppress("BooleanLiteralArgument") // Not allowed for expected classes
                if (isFinished.compareAndSet(false, true)) {
                    action()
                }
            }

        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(
            object : Disposable by disposableWrapper {
                override fun dispose() {
                    observer.tryCatch(onFinally)
                    disposableWrapper.dispose()
                }
            }
        )

        subscribeSafe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    observer.tryCatch(onFinally) {
                        observer.onSuccess(value)
                    }
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch(onFinally, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }
