package com.badoo.reaktive.maybe

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.completable.CompletableCallbacks
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.disposable
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.utils.handleSourceError

fun <T> Maybe<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onSubscribe(disposableWrapper)
            observer.onError(e)
            disposableWrapper.dispose()

            return@maybeUnsafe
        }

        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    disposableWrapper.doIfNotDisposed(dispose = true, block = observer::onComplete)
                }

                override fun onSuccess(value: T) {
                    disposableWrapper.doIfNotDisposed(dispose = true) {
                        observer.onSuccess(value)
                    }
                }

                override fun onError(error: Throwable) {
                    disposableWrapper.doIfNotDisposed(dispose = true) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeSuccess(consumer: (T) -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribeSafe(
            object : MaybeObserver<T>, CompletableCallbacks by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onSuccess(value: T) {
                    if (!emitter.isDisposed) {
                        emitter.tryCatch({ consumer(value) }) {
                            emitter.onSuccess(value)
                        }
                    }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeComplete(action: () -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribeSafe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, ErrorCallback by emitter {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    tryCatch(action) {
                        emitter.onComplete()
                    }
                }
            }
        )
    }

fun <T> Maybe<T>.doOnBeforeError(consumer: (Throwable) -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribeSafe(
            object : MaybeObserver<T>, SuccessCallback<T> by emitter, CompleteCallback by emitter {
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

fun <T> Maybe<T>.doOnBeforeTerminate(action: () -> Unit): Maybe<T> =
    maybe { emitter ->
        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    emitter.setDisposable(disposable)
                }

                override fun onComplete() {
                    emitter.tryCatch(action) {
                        emitter.onComplete()
                    }
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

fun <T> Maybe<T>.doOnBeforeDispose(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        disposables +=
            disposable {
                try {
                    action()
                } catch (e: Throwable) {
                    handleSourceError(e) // Can't send error to downstream, already disposed
                }
            }

        subscribeSafe(
            object : MaybeObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposables += disposable
                }

                override fun onComplete() {
                    onUpstreamFinished(observer::onComplete)
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

fun <T> Maybe<T>.doOnBeforeFinally(action: () -> Unit): Maybe<T> =
    maybeUnsafe { observer ->
        val disposables = CompositeDisposable()
        observer.onSubscribe(disposables)

        disposables +=
            disposable {
                try {
                    action()
                } catch (e: Throwable) {
                    handleSourceError(e) // Can't send error to downstream, already disposed
                }
            }

        subscribeSafe(
            object : MaybeObserver<T> {
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
