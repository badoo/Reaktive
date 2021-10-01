package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
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
fun <T> Observable<T>.doOnAfterSubscribe(action: (Disposable) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val serialDisposable = SerialDisposable()

        observer.onSubscribe(serialDisposable)

        try {
            action(serialDisposable)
        } catch (e: Throwable) {
            observer.onError(e)
            serialDisposable.dispose()

            return@observableUnsafe
        }

        subscribeSafe(
            object : ObservableObserver<T>, ValueCallback<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    serialDisposable.set(disposable)
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
 * Calls the [consumer] with the emitted element when the [Observable] signals `onNext`.
 * The [consumer] is called **after** the observer is called.
 */
fun <T> Observable<T>.doOnAfterNext(consumer: (T) -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(value)
                        emitter.tryCatch { consumer(value) }
                    }
                }
            }
        )
    }

/**
 * Calls the [action] when the [Observable] signals `onComplete`.
 * The [action] is called **after** the observer is called.
 */
fun <T> Observable<T>.doOnAfterComplete(action: () -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter, ErrorCallback by emitter {
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
 * Calls the [consumer] with the emitted [Throwable] when the [Observable] signals `onError`.
 * The [consumer] is called **after** the observer is called.
 */
fun <T> Observable<T>.doOnAfterError(consumer: (Throwable) -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter, CompleteCallback by emitter {
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
 * Calls the [action] when the [Observable] signals a terminal event: `onComplete` or `onError`.
 * The [action] is called **after** the observer is called.
 */
fun <T> Observable<T>.doOnAfterTerminate(action: () -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
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
fun <T> Observable<T>.doOnAfterDispose(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : ObservableObserver<T>, ValueCallback<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable

                    disposables +=
                        Disposable {
                            // Can't send error to downstream, already disposed
                            tryCatchAndHandle(block = action)
                        }
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
 * - The [Observable] signals a terminal event: `onComplete` or `onError` (the [action] is called **after** the observer is called).
 * - The [Disposable] sent to the observer via `onSubscribe` is disposed (the [action] is called **after** the upstream is disposed).
 */
fun <T> Observable<T>.doOnAfterFinally(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : ObservableObserver<T>, ValueCallback<T> by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable

                    disposables +=
                        Disposable {
                            // Can't send error to downstream, already disposed
                            tryCatchAndHandle(block = action)
                        }
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
