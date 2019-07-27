package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun <T> Maybe<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onSubscribe(disposableWrapper)
            observer.onError(e)

            return@maybeUnsafe
        }

        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, MaybeCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeSuccess(consumer: (T) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, CompletableCallbacks by observer {
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

fun <T> Maybe<T>.doOnBeforeComplete(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, SuccessCallback<T> by observer, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    observer.tryCatch(action) {
                        observer.onComplete()
                    }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, SuccessCallback<T> by observer, CompleteCallback by observer {
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

fun <T> Maybe<T>.doOnBeforeTerminate(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    observer.tryCatch(action) {
                        observer.onSuccess(value)
                    }
                }

                override fun onComplete() {
                    observer.tryCatch(action) {
                        observer.onComplete()
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

fun <T> Maybe<T>.doOnBeforeDispose(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
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
            object : MaybeObserver<T>, MaybeCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeFinally(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
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
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    observer.tryCatch(onFinally) {
                        observer.onSuccess(value)
                    }
                }

                override fun onComplete() {
                    observer.tryCatch(onFinally) {
                        observer.onComplete()
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
