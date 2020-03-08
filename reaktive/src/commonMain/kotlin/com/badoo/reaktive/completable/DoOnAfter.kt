package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatchAndHandle
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.disposable.plusAssign

fun Completable.doOnAfterSubscribe(action: (Disposable) -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        observer.onSubscribe(disposableWrapper)

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onError(e)
            disposableWrapper.dispose()

            return@completableUnsafe
        }

        subscribeSafe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
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

fun Completable.doOnAfterComplete(action: () -> Unit): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, ErrorCallback by emitter {
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

fun Completable.doOnAfterError(consumer: (Throwable) -> Unit): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver, CompleteCallback by emitter {
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

fun Completable.doOnAfterTerminate(action: () -> Unit): Completable =
    completable { emitter ->
        subscribe(
            object : CompletableObserver {
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

fun Completable.doOnAfterDispose(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : CompletableObserver {
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

fun Completable.doOnAfterFinally(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        subscribeSafe(
            object : CompletableObserver {
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
