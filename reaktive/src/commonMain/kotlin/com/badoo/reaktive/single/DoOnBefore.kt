package com.badoo.reaktive.single

import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.SuccessCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.CompositeDisposable
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.disposable.doIfNotDisposed
import com.badoo.reaktive.disposable.plusAssign
import com.badoo.reaktive.utils.handleReaktiveError

fun <T> Single<T>.doOnBeforeSubscribe(action: (Disposable) -> Unit): Single<T> =
    singleUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onSubscribe(disposableWrapper)
            observer.onError(e)
            disposableWrapper.dispose()

            return@singleUnsafe
        }

        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : SingleObserver<T> {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
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
