package com.badoo.reaktive.observable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.ValueCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
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
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#doOnSubscribe-io.reactivex.functions.Consumer-).
 */
fun <T> Observable<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Observable<T> =
    observableUnsafe { observer ->
        val serialDisposable = SerialDisposable()

        try {
            action(serialDisposable)
        } catch (e: Throwable) {
            observer.onSubscribe(serialDisposable)
            observer.onError(e)
            serialDisposable.dispose()

            return@observableUnsafe
        }

        observer.onSubscribe(serialDisposable)

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
 * Calls the [consumer] with the emitted value when the [Observable] signals `onNext`.
 * The [consumer] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#doOnNext-io.reactivex.functions.Consumer-).
 */
fun <T> Observable<T>.doOnBeforeNext(consumer: (T) -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onNext(value: T) {
                    if (!emitter.isDisposed) {
                        emitter.tryCatch(block = { consumer(value) }) {
                            emitter.onNext(value)
                        }
                    }
                }
            }
        )
    }

/**
 * Calls the [action] with the emitted value when the [Observable] signals `onComplete`.
 * The [action] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#doOnComplete-io.reactivex.functions.Action-).
 */
fun <T> Observable<T>.doOnBeforeComplete(action: () -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter, ErrorCallback by emitter {
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
 * Calls the [consumer] with the emitted [Throwable] when the [Observable] signals `onError`.
 * The [consumer] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#doOnError-io.reactivex.functions.Consumer-).
 */
fun <T> Observable<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter, CompleteCallback by emitter {
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
 * Calls the [action] when the [Observable] signals a terminal event: `onComplete` or `onError`.
 * The [action] is called **before** the observer is called.
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#doOnTerminate-io.reactivex.functions.Action-).
 */
fun <T> Observable<T>.doOnBeforeTerminate(action: () -> Unit): Observable<T> =
    observable { emitter ->
        subscribe(
            object : ObservableObserver<T>, ValueCallback<T> by emitter {
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
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#doOnDispose-io.reactivex.functions.Action-).
 */
fun <T> Observable<T>.doOnBeforeDispose(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
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
            object : ObservableObserver<T>, ValueCallback<T> by observer {
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
 * - The [Observable] signals a terminal event: `onComplete` or `onError` (the [action] is called **before** the observer is called).
 * - The [Disposable] sent to the observer via `onSubscribe` is disposed (the [action] is called **before** the upstream is disposed).
 *
 * Please refer to the corresponding RxJava [document](http://reactivex.io/RxJava/javadoc/io/reactivex/Observable.html#doFinally-io.reactivex.functions.Action-).
 */
fun <T> Observable<T>.doOnBeforeFinally(action: () -> Unit): Observable<T> =
    observableUnsafe { observer ->
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
            object : ObservableObserver<T>, ValueCallback<T> by observer {
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
