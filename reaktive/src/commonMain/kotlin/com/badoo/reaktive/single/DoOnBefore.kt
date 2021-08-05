package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.SerialDisposable
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.handleReaktiveError

/**
 * Calls the shared [action] for each new observer with the [Disposable] sent to the downstream.
 * The [action] is called for each new observer **before** its `onSubscribe` callback is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#doOnSubscribe-io.reactivex.functions.Consumer-).
 */
fun <T> Single<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val serialDisposable = SerialDisposable()

        try {
            action(serialDisposable)
        } catch (e: Throwable) {
            observer.onSubscribe(serialDisposable)
            observer.onError(e)
            serialDisposable.dispose()

            return@singleUnsafe
        }

        observer.onSubscribe(serialDisposable)

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
 * Calls the [consumer] with the emitted value when the [Single] signals `onSuccess`.
 * The [consumer] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#doOnSuccess-io.reactivex.functions.Consumer-).
 */
fun <T> Single<T>.doOnBeforeSuccess(consumer: (T) -> Unit): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    if (!emitter.isDisposed) {
                        emitter.tryCatch(block = { consumer(value) }) {
                            emitter.onSuccess(value)
                        }
                    }
                }
            }
        )
    }

/**
 * Calls the [consumer] with the emitted [Throwable] when the [Single] signals `onError`.
 * The [consumer] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#doOnError-io.reactivex.functions.Consumer-).
 */
fun <T> Single<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T>, SuccessCallback<T> by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch({ consumer(error) }, { CompositeException(error, it) }) {
                        emitter.onError(error)
                    }
                }
            }
        )
    }

/**
 * Calls the [action] when the [Single] signals a terminal event: either `onSuccess` or `onError`.
 * The [action] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#doOnTerminate-io.reactivex.functions.Action-).
 */
fun <T> Single<T>.doOnBeforeTerminate(action: () -> Unit): Single<T> =
    single { emitter ->
        subscribe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    emitter.tryCatch(action) {
                        emitter.onSuccess(value)
                    }
                }

                override fun onError(error: Throwable) {
                    emitter.tryCatch(action, { CompositeException(error, it) }) {
                        emitter.onError(error)
                    }
                }
            }
        )
    }

/**
 * Calls the shared [action] when the [Disposable] sent to the observer via `onSubscribe` is disposed.
 * The [action] is called **before** the upstream is disposed.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#doOnDispose-io.reactivex.functions.Action-).
 */
fun <T> Single<T>.doOnBeforeDispose(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        disposables +=
            Disposable {
                try {
                    action()
                } catch (e: Throwable) {
                    handleReaktiveError(e) // Can't send error to downstream, already disposed
                }
            }

        subscribeSafe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
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
 * - The [Single] signals a terminal event: either `onSuccess` or `onError` (the [action] is called **before** the observer is called).
 * - The [Disposable] sent to the observer via `onSubscribe` is disposed (the [action] is called **before** the upstream is disposed).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Single.html#doFinally-io.reactivex.functions.Action-).
 */
fun <T> Single<T>.doOnBeforeFinally(action: () -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        disposables +=
            Disposable {
                try {
                    action()
                } catch (e: Throwable) {
                    handleReaktiveError(e) // Can't send error to downstream, already disposed
                }
            }

        subscribeSafe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onSuccess(value: T) {
                    onUpstreamFinished {
                        observer.tryCatch(action) {
                            observer.onSuccess(value)
                        }
                    }
                }

                override fun onError(error: Throwable) {
                    onUpstreamFinished {
                        observer.tryCatch(action, { CompositeException(error, it) }) {
                            observer.onError(error)
                        }
                    }
                }

                private inline fun onUpstreamFinished(block: () -> Unit) {
                    try {
                        disposables.clear(false) // Prevent "action" from being called while disposing
                        block()
                    } finally {
                        disposables.dispose()
                    }
                }
            }
        )
    }
