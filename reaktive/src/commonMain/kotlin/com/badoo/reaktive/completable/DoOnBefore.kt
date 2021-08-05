package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
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
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#doOnSubscribe-io.reactivex.functions.Consumer-).
 */
fun Completable.doOnBeforeSubscribe(action: (Disposable) -> Unit): Completable =
    completableUnsafe { observer ->
        val serialDisposable = SerialDisposable()

        try {
            action(serialDisposable)
        } catch (e: Throwable) {
            observer.onSubscribe(serialDisposable)
            observer.onError(e)
            serialDisposable.dispose()

            return@completableUnsafe
        }

        observer.onSubscribe(serialDisposable)

        subscribeSafe(
            object : CompletableObserver {
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
 * Calls the [action] with the emitted value when the [Completable] signals `onComplete`.
 * The [action] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#doOnComplete-io.reactivex.functions.Action-).
 */
fun Completable.doOnBeforeComplete(action: () -> Unit): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    emitter.tryCatch(action) {
                        emitter.onComplete()
                    }
                }
            }
        )
    }

/**
 * Calls the [consumer] with the emitted [Throwable] when the [Completable] signals `onError`.
 * The [consumer] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#doOnError-io.reactivex.functions.Consumer-).
 */
fun Completable.doOnBeforeError(consumer: (Throwable) -> Unit): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, CompleteCallback by emitter {
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
 * Calls the [action] when the [Completable] signals a terminal event: either `onComplete` or `onError`.
 * The [action] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#doOnTerminate-io.reactivex.functions.Action-).
 */
fun Completable.doOnBeforeTerminate(action: () -> Unit): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    emitter.tryCatch(action) {
                        emitter.onComplete()
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
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#doOnDispose-io.reactivex.functions.Action-).
 */
fun Completable.doOnBeforeDispose(action: () -> Unit): Completable =
    completableUnsafe { observer ->
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
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
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
 * - The [Completable] signals a terminal event: either `onComplete` or `onError` (the [action] is called **before** the observer is called).
 * - The [Disposable] sent to the observer via `onSubscribe` is disposed (the [action] is called **before** the upstream is disposed).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Completable.html#doFinally-io.reactivex.functions.Action-).
 */
fun Completable.doOnBeforeFinally(action: () -> Unit): Completable =
    completableUnsafe { observer ->
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
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onComplete() {
                    onUpstreamFinished {
                        observer.tryCatch(action) {
                            observer.onComplete()
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
