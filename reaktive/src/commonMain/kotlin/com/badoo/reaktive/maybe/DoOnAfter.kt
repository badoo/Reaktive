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
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.disposable.plusAssign

/**
 * Calls the shared [action] for each new observer with the [Disposable] sent to the downstream.
 * The [action] is called for each new observer **after** its `onSubscribe` callback is called.
 */
fun <T> Maybe<T>.doOnAfterSubscribe(action: (Disposable) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val serialDisposable = SerialDisposable()

        observer.onSubscribe(serialDisposable)

        try {
            action(serialDisposable)
        } catch (e: Throwable) {
            observer.onError(e)
            serialDisposable.dispose()

            return@maybeUnsafe
        }

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
                }

                override fun onSuccess(value: T) {
                    serialDisposable.doIfNotDisposed(dispose = true) {
                        observer.onSuccess(value)
                    }
                }

                override fun onComplete() {
                    serialDisposable.doIfNotDisposed(dispose = true, block = observer::onComplete)
                }

                override fun onError(error: Throwable) {
                    serialDisposable.doIfNotDisposed(dispose = true) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

/**
 * Calls the [action] with the emitted value when the [Maybe] signals `onSuccess`.
 * The [action] is called **after** the observer is called.
 */
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

/**
 * Calls the [action] when the [Maybe] signals `onComplete`.
 * The [action] is called **after** the observer is called.
 */
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

/**
 * Calls the [consumer] with the emitted [Throwable] when the [Maybe] signals `onError`.
 * The [consumer] is called **after** the observer is called.
 */
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

/**
 * Calls the [action] when the [Maybe] signals a terminal event: `onSuccess`, `onComplete` or `onError`.
 * The [action] is called **after** the observer is called.
 */
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

/**
 * Calls the shared [action] when the [Disposable] sent to the observer via `onSubscribe` is disposed.
 * The [action] is called **after** the upstream is disposed.
 */
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

/**
 * Calls the [action] when one of the following events occur:
 * - The [Maybe] signals a terminal event: `onSuccess`, `onComplete` or `onError` (the [action] is called **after** the observer is called).
 * - The [Disposable] sent to the observer via `onSubscribe` is disposed (the [action] is called **after** the upstream is disposed).
 */
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
