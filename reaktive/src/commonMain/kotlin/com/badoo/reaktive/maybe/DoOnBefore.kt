package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.wrap
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.utils.atomicreference.AtomicReference

fun <T> Maybe<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T>, MaybeCallbacks<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                    action(disposable)
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
                    consumer(value)
                    observer.onSuccess(value)
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
                    action()
                    observer.onComplete()
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
                    consumer(error)
                    observer.onError(error)
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
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper.wrap(onBeforeDispose = action))

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
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
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
            }
        )
    }
