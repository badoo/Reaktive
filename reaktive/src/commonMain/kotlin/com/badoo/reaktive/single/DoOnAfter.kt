package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatchAndHandle
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.disposable.plusAssign

/**
 * Calls the shared [action] for each new observer with the [Disposable] sent to the downstream.
 * The [action] is called for each new observer **after** its `onSubscribe` callback is called.
 */
fun <T> Single<T>.doOnAfterSubscribe(action: (Disposable) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val serialDisposable = SerialDisposable()

        observer.onSubscribe(serialDisposable)

        try {
            action(serialDisposable)
        } catch (e: Throwable) {
            observer.onError(e)
            serialDisposable.dispose()

            return@singleUnsafe
        }

        subscribeSafe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
                }

                override fun onSuccess(value: T) {
                    serialDisposable.doIfNotDisposed(dispose = true) {
                        observer.onSuccess(value)
                    }
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
 * Calls the [action] with the emitted value when the [Single] signals `onSuccess`.
 * The [action] is called **after** the observer is called.
 */
fun <T> Single<T>.doOnAfterSuccess(action: (T) -> Unit): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, ErrorCallback by emitter {
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
 * Calls the [consumer] with the emitted [Throwable] when the [Single] signals `onError`.
 * The [consumer] is called **after** the observer is called.
 */
fun <T> Single<T>.doOnAfterError(consumer: (Throwable) -> Unit): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, SuccessCallback<T> by emitter {
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
 * Calls the [action] when the [Single] signals a terminal event: either `onSuccess` or `onError`.
 * The [action] is called **after** the observer is called.
 */
fun <T> Single<T>.doOnAfterTerminate(action: () -> Unit): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.onSuccess(value)

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
fun <T> Single<T>.doOnAfterDispose(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : SingleObserver<T> {
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
 * - The [Single] signals a terminal event: either `onSuccess` or `onError` (the [action] is called **after** the observer is called).
 * - The [Disposable] sent to the observer via `onSubscribe` is disposed (the [action] is called **after** the upstream is disposed).
 */
fun <T> Single<T>.doOnAfterFinally(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : SingleObserver<T> {
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
