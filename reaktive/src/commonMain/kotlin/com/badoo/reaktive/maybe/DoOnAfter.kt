package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatchAndHandle
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.disposable.plusAssign

fun <T> Maybe<T>.doOnAfterSubscribe(action: (Disposable) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        observer.onSubscribe(disposableWrapper)

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onError(e)
            disposableWrapper.dispose()

            return@maybeUnsafe
        }

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onSuccess(value: T) {
                    disposableWrapper.doIfNotDisposed(dispose = true) {
                        observer.onSuccess(value)
                    }
                }

                override fun onComplete() {
                    disposableWrapper.doIfNotDisposed(dispose = true, block = observer::onComplete)
                }

                override fun onError(error: Throwable) {
                    disposableWrapper.doIfNotDisposed(dispose = true) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnAfterSuccess(action: (T) -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.onSuccess(value)

                    // Can't send error to downstream, already terminated with onComplete
                    tryCatchAndHandle { action(value) }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnAfterComplete(action: () -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    emitter.onComplete()

                    // Can't send error to downstream, already terminated with onComplete
                    tryCatchAndHandle(block = action)
                }
            }
        )
    }

fun <T> Maybe<T>.doOnAfterError(consumer: (Throwable) -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, CompleteCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.onError(error)

                    // Can't send error to the downstream, already terminated with onError
                    tryCatchAndHandle({ CompositeException(error, it) }) {
                        consumer(error)
                    }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnAfterTerminate(action: () -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.onSuccess(value)

                    // Can't send error to the downstream, already terminated with onComplete
                    tryCatchAndHandle(block = action)
                }

                override fun onComplete() {
                    emitter.onComplete()

                    // Can't send error to the downstream, already terminated with onComplete
                    tryCatchAndHandle(block = action)
                }

                override fun onError(error: Throwable) {
                    emitter.onError(error)

                    // Can't send error to the downstream, already terminated with onError
                    tryCatchAndHandle({ CompositeException(error, it) }, action)
                }
            }
        )
    }

fun <T> Maybe<T>.doOnAfterDispose(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable

                    disposables +=
                        Disposable {
                            // Can't send error to downstream, already disposed
                            tryCatchAndHandle(block = action)
                        }
                }

                override fun onSuccess(value: T) {
                    onUpstreamFinished { observer.onSuccess(value) }
                }

                override fun onComplete() {
                    onUpstreamFinished(observer::onComplete)
                }

                override fun onError(error: Throwable) {
                    onUpstreamFinished { observer.onError(error) }
                }

                private inline fun onUpstreamFinished(block: () -> Unit) {
                    try {
                        disposables.clear(false) // Prevent "action" from being called
                        block()
                    } finally {
                        disposables.dispose()
                    }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnAfterFinally(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable

                    disposables +=
                        Disposable {
                            // Can't send error to downstream, already disposed
                            tryCatchAndHandle(block = action)
                        }
                }

                override fun onSuccess(value: T) {
                    onUpstreamFinished { observer.onSuccess(value) }

                    // Can't send error to the downstream, already terminated with onComplete
                    tryCatchAndHandle(block = action)
                }

                override fun onComplete() {
                    onUpstreamFinished(block = observer::onComplete)

                    // Can't send error to the downstream, already terminated with onComplete
                    tryCatchAndHandle(block = action)
                }

                override fun onError(error: Throwable) {
                    onUpstreamFinished {
                        observer.onError(error)

                        // Can't send error to the downstream, already terminated with onError
                        tryCatchAndHandle({ CompositeException(error, it) }, action)
                    }
                }

                private inline fun onUpstreamFinished(block: () -> Unit) {
                    disposables.clear(false) // Prevent "action" from being called while disposing
                    try {
                        block()
                    } finally {
                        disposables.dispose()
                    }
                }
            }
        )
    }
