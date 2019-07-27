package com.badoo.reaktive.completable

import com.badoo.reaktive.base.CompleteCallback
import com.badoo.reaktive.base.ErrorCallback
import com.badoo.reaktive.base.exceptions.CompositeException
import com.badoo.reaktive.base.subscribeSafe
import com.badoo.reaktive.base.tryCatch
import com.badoo.reaktive.disposable.Disposable
import com.badoo.reaktive.disposable.DisposableWrapper
import com.badoo.reaktive.utils.atomic.AtomicBoolean

fun Completable.doOnBeforeSubscribe(action: (Disposable) -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()

        try {
            action(disposableWrapper)
        } catch (e: Throwable) {
            observer.onSubscribe(disposableWrapper)
            observer.onError(e)

            return@completableUnsafe
        }

        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun Completable.doOnBeforeComplete(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, ErrorCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    observer.tryCatch(action) {
                        observer.onComplete()
                    }
                }
            }
        )
    }

fun Completable.doOnBeforeError(consumer: (Throwable) -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver, CompleteCallback by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch({ consumer(error) }, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

fun Completable.doOnBeforeTerminate(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(disposableWrapper)

        subscribeSafe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    observer.tryCatch(action) {
                        observer.onComplete()
                    }
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch(action, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }

fun Completable.doOnBeforeDispose(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(
            object : Disposable by disposableWrapper {
                override fun dispose() {
                    observer.tryCatch(action)
                    disposableWrapper.dispose()
                }
            }
        )

        subscribeSafe(
            object : CompletableObserver, CompletableCallbacks by observer {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }
            }
        )
    }

fun Completable.doOnBeforeFinally(action: () -> Unit): Completable =
    completableUnsafe { observer ->
        val isFinished = AtomicBoolean()

        val onFinally =
            {
                @Suppress("BooleanLiteralArgument") // Not allowed for expected classes
                if (isFinished.compareAndSet(false, true)) {
                    action()
                }
            }

        val disposableWrapper = DisposableWrapper()
        observer.onSubscribe(
            object : Disposable by disposableWrapper {
                override fun dispose() {
                    observer.tryCatch(onFinally)
                    disposableWrapper.dispose()
                }
            }
        )

        subscribeSafe(
            object : CompletableObserver {
                override fun onSubscribe(disposable: Disposable) {
                    disposableWrapper.set(disposable)
                }

                override fun onComplete() {
                    observer.tryCatch(onFinally) {
                        observer.onComplete()
                    }
                }

                override fun onError(error: Throwable) {
                    observer.tryCatch(onFinally, { CompositeException(error, it) }) {
                        observer.onError(error)
                    }
                }
            }
        )
    }
